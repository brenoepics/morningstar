package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.campaign.calendar.CalendarCampaign;
import com.eu.habbo.habbohotel.catalog.TargetOffer;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.modtool.ModToolSanctionItem;
import com.eu.habbo.habbohotel.modtool.ModToolSanctions;
import com.eu.habbo.habbohotel.navigation.NavigatorSavedSearch;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.RoomManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.habbohotel.users.clothingvalidation.ClothingValidationManager;
import com.eu.habbo.habbohotel.users.subscriptions.SubscriptionHabboClub;
import com.eu.habbo.messages.NoAuthMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.TargetedOfferComposer;
import com.eu.habbo.messages.outgoing.events.calendar.AdventCalendarDataComposer;
import com.eu.habbo.messages.outgoing.gamecenter.GameCenterAccountInfoComposer;
import com.eu.habbo.messages.outgoing.gamecenter.GameCenterGameListComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.MessagesForYouComposer;
import com.eu.habbo.messages.outgoing.habboway.nux.NewUserIdentityComposer;
import com.eu.habbo.messages.outgoing.habboway.nux.NuxAlertComposer;
import com.eu.habbo.messages.outgoing.handshake.EnableNotificationsComposer;
import com.eu.habbo.messages.outgoing.handshake.SecureLoginOKComposer;
import com.eu.habbo.messages.outgoing.handshake.AvailabilityStatusMessageComposer;
import com.eu.habbo.messages.outgoing.handshake.PingComposer;
import com.eu.habbo.messages.outgoing.inventory.BadgePointLimitsComposer;
import com.eu.habbo.messages.outgoing.inventory.UserEffectsListComposer;
import com.eu.habbo.messages.outgoing.modtool.CfhTopicsMessageComposer;
import com.eu.habbo.messages.outgoing.modtool.ModToolComposer;
import com.eu.habbo.messages.outgoing.modtool.ModToolSanctionInfoComposer;
import com.eu.habbo.messages.outgoing.navigator.*;
import com.eu.habbo.messages.outgoing.unknown.BuildersClubExpiredComposer;
import com.eu.habbo.messages.outgoing.mysterybox.MysteryBoxKeysComposer;
import com.eu.habbo.messages.outgoing.users.*;
import com.eu.habbo.plugin.events.emulator.SSOAuthenticationEvent;
import com.eu.habbo.plugin.events.users.UserLoginEvent;
import gnu.trove.map.hash.THashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static java.time.temporal.ChronoUnit.DAYS;

@NoAuthMessage
public class SecureLoginEvent extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecureLoginEvent.class);


    @Override
    public void handle() throws Exception {
        if (!this.client.getChannel().isOpen()) {
            Emulator.getGameServer().getGameClientManager().disposeClient(this.client);
            return;
        }

        if (!Emulator.isReady)
            return;

        if (Emulator.getConfig().getBoolean("encryption.forced", false) && Emulator.getCrypto().isEnabled() && !this.client.isHandshakeFinished()) {
            Emulator.getGameServer().getGameClientManager().disposeClient(this.client);
            LOGGER.warn("Encryption is forced and TLS Handshake isn't finished! Closed connection...");
            return;
        }

        String sso = this.packet.readString().replace(" ", "");

        if (Emulator.getPluginManager().fireEvent(new SSOAuthenticationEvent(sso)).isCancelled()) {
            Emulator.getGameServer().getGameClientManager().disposeClient(this.client);
            LOGGER.info("SSO Authentication is cancelled by a plugin. Closed connection...");
            return;
        }

        if (sso.isEmpty()) {
            Emulator.getGameServer().getGameClientManager().disposeClient(this.client);
            LOGGER.debug("Client is trying to connect without SSO ticket! Closed connection...");
            return;
        }

        if (this.client.getHabbo() == null) {
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().loadHabbo(sso);
            if (habbo != null) {
                try {
                    habbo.setClient(this.client);
                    this.client.setHabbo(habbo);
                    if (!this.client.getHabbo().connect()) {
                        Emulator.getGameServer().getGameClientManager().disposeClient(this.client);
                        return;
                    }

                    if (this.client.getHabbo().getHabboInfo() == null) {
                        Emulator.getGameServer().getGameClientManager().disposeClient(this.client);
                        return;
                    }

                    if (this.client.getHabbo().getHabboInfo().getRank() == null) {
                        throw new NullPointerException(habbo.getHabboInfo().getUsername() + " has a NON EXISTING RANK!");
                    }

                    Emulator.getThreading().run(habbo);
                    Emulator.getGameEnvironment().getHabboManager().addHabbo(habbo);
                } catch (Exception e) {
                    LOGGER.error("Caught exception", e);
                    Emulator.getGameServer().getGameClientManager().disposeClient(this.client);
                    return;
                }

                if (ClothingValidationManager.VALIDATE_ON_LOGIN) {
                    String validated = ClothingValidationManager.validateLook(this.client.getHabbo());
                    if (!validated.equals(this.client.getHabbo().getHabboInfo().getLook())) {
                        this.client.getHabbo().getHabboInfo().setLook(validated);
                    }
                }

                ArrayList<ServerMessage> messages = new ArrayList<>();

                messages.add(new SecureLoginOKComposer().compose());

                int roomIdToEnter = 0;

                if (!this.client.getHabbo().getHabboStats().nux || Emulator.getConfig().getBoolean("retro.style.homeroom") && this.client.getHabbo().getHabboInfo().getHomeRoom() != 0)
                    roomIdToEnter = this.client.getHabbo().getHabboInfo().getHomeRoom();
                else if (!this.client.getHabbo().getHabboStats().nux || Emulator.getConfig().getBoolean("retro.style.homeroom") && RoomManager.HOME_ROOM_ID > 0)
                    roomIdToEnter = RoomManager.HOME_ROOM_ID;

                boolean calendar = false;
                if (!this.client.getHabbo().getHabboStats().getAchievementProgress().containsKey(Emulator.getGameEnvironment().getAchievementManager().getAchievement("Login"))) {
                    AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("Login"));
                    calendar = true;
                } else {
                    int previousOnline = (int)this.client.getHabbo().getHabboStats().cache.get("previousOnline");
                    long daysBetween = ChronoUnit.DAYS.between(new Date((long) previousOnline * 1000L).toInstant(), new Date().toInstant());

                    Date lastLogin = new Date(previousOnline);
                    Calendar c1 = Calendar.getInstance();
                    c1.add(Calendar.DAY_OF_YEAR, -1);

                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(lastLogin);

                    if (daysBetween == 1) {
                        if (this.client.getHabbo().getHabboStats().getAchievementProgress().get(Emulator.getGameEnvironment().getAchievementManager().getAchievement("Login")) == this.client.getHabbo().getHabboStats().loginStreak) {
                            AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("Login"));
                        }
                        this.client.getHabbo().getHabboStats().loginStreak++;
                        calendar = true;
                    } else if (daysBetween >= 1) {
                        calendar = true;
                    } else {
                        if (((lastLogin.getTime() / 1000) - Emulator.getIntUnixTimestamp()) > 86400) {
                            this.client.getHabbo().getHabboStats().loginStreak = 0;
                        }
                    }
                }

                if (!this.client.getHabbo().getHabboStats().getAchievementProgress().containsKey(Emulator.getGameEnvironment().getAchievementManager().getAchievement("RegistrationDuration"))) {
                    AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("RegistrationDuration"), 0);
                } else {
                    int daysRegistered = ((Emulator.getIntUnixTimestamp() - this.client.getHabbo().getHabboInfo().getAccountCreated()) / 86400);

                    int days = this.client.getHabbo().getHabboStats().getAchievementProgress(
                            Emulator.getGameEnvironment().getAchievementManager().getAchievement("RegistrationDuration")
                    );

                    if (daysRegistered - days > 0) {
                        AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("RegistrationDuration"), daysRegistered - days);
                    }
                }

                if (!this.client.getHabbo().getHabboStats().getAchievementProgress().containsKey(Emulator.getGameEnvironment().getAchievementManager().getAchievement("TraderPass"))) {
                    AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("TraderPass"));
                }


                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement achievementQueueStatement = connection.prepareStatement("SELECT * FROM users_achievements_queue WHERE user_id = ?")) {
                    achievementQueueStatement.setInt(1, this.client.getHabbo().getHabboInfo().getId());

                    try (ResultSet achievementSet = achievementQueueStatement.executeQuery()) {
                        while (achievementSet.next()) {
                            AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement(achievementSet.getInt("achievement_id")), achievementSet.getInt("amount"));
                        }
                    }

                    try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM users_achievements_queue WHERE user_id = ?")) {
                        deleteStatement.setInt(1, this.client.getHabbo().getHabboInfo().getId());
                        deleteStatement.execute();
                    }
                }

                messages.add(new UserHomeRoomComposer(this.client.getHabbo().getHabboInfo().getHomeRoom(), roomIdToEnter).compose());
                messages.add(new UserEffectsListComposer(habbo, this.client.getHabbo().getInventory().getEffectsComponent().effects.values()).compose());
                messages.add(new UserClothesComposer(this.client.getHabbo()).compose());
                messages.add(new NewUserIdentityComposer(habbo).compose());
                messages.add(new UserPermissionsComposer(this.client.getHabbo()).compose());
                messages.add(new AvailabilityStatusMessageComposer(true, false, true).compose());
                messages.add(new PingComposer().compose());
                messages.add(new EnableNotificationsComposer(Emulator.getConfig().getBoolean("bubblealerts.enabled", true)).compose());
                messages.add(new UserAchievementScoreComposer(this.client.getHabbo()).compose());
                messages.add(new IsFirstLoginOfDayComposer(true).compose());
                messages.add(new MysteryBoxKeysComposer().compose());
                messages.add(new BuildersClubExpiredComposer().compose());
                messages.add(new CfhTopicsMessageComposer().compose());
                messages.add(new FavoriteRoomsCountComposer(this.client.getHabbo()).compose());
                messages.add(new GameCenterGameListComposer().compose());
                messages.add(new GameCenterAccountInfoComposer(3, 100).compose());
                messages.add(new GameCenterAccountInfoComposer(0, 100).compose());

                messages.add(new UserClubComposer(this.client.getHabbo(), SubscriptionHabboClub.HABBO_CLUB, UserClubComposer.RESPONSE_TYPE_LOGIN).compose());

                if (this.client.getHabbo().hasPermission(Permission.ACC_SUPPORTTOOL)) {
                    messages.add(new ModToolComposer(this.client.getHabbo()).compose());
                }


                CalendarCampaign campaign = Emulator.getGameEnvironment().getCalendarManager().getCalendarCampaign(Emulator.getConfig().getValue("hotel.calendar.default"));
                if (campaign != null) {
                    long daysBetween = DAYS.between(new Timestamp(campaign.getStartTimestamp() * 1000L).toInstant(), new Date().toInstant());
                    if (daysBetween >= 0) {
                        messages.add(new AdventCalendarDataComposer(campaign.getName(), campaign.getImage(), campaign.getTotalDays(), (int) daysBetween, this.client.getHabbo().getHabboStats().calendarRewardsClaimed, campaign.getLockExpired()).compose());
                        if(Emulator.getConfig().getBoolean("hotel.login.show.calendar", false)) {
                            messages.add(new NuxAlertComposer("openView/calendar").compose());
                        }
                    }
                }

                if (TargetOffer.ACTIVE_TARGET_OFFER_ID > 0) {
                    TargetOffer offer = Emulator.getGameEnvironment().getCatalogManager().getTargetOffer(TargetOffer.ACTIVE_TARGET_OFFER_ID);

                    if (offer != null) {
                        messages.add(new TargetedOfferComposer(this.client.getHabbo(), offer).compose());
                    }
                }

                this.client.sendResponses(messages);

                //Hardcoded
                //this.client.sendResponse(new ForumsTestComposer());
                this.client.sendResponse(new BadgePointLimitsComposer());

                ModToolSanctions modToolSanctions = Emulator.getGameEnvironment().getModToolSanctions();

                if (Emulator.getConfig().getBoolean("hotel.sanctions.enabled")) {
                    THashMap<Integer, ArrayList<ModToolSanctionItem>> modToolSanctionItemsHashMap = Emulator.getGameEnvironment().getModToolSanctions().getSanctions(habbo.getHabboInfo().getId());
                    ArrayList<ModToolSanctionItem> modToolSanctionItems = modToolSanctionItemsHashMap.get(habbo.getHabboInfo().getId());

                    if (modToolSanctionItems != null && modToolSanctionItems.size() > 0) {
                        ModToolSanctionItem item = modToolSanctionItems.get(modToolSanctionItems.size() - 1);

                        if (item.sanctionLevel > 0 && item.probationTimestamp != 0 && item.probationTimestamp > Emulator.getIntUnixTimestamp()) {
                            this.client.sendResponse(new ModToolSanctionInfoComposer(this.client.getHabbo()));
                        } else if (item.sanctionLevel > 0 && item.probationTimestamp != 0 && item.probationTimestamp <= Emulator.getIntUnixTimestamp()) {
                            modToolSanctions.updateSanction(item.id, 0);
                        }

                        if (item.tradeLockedUntil > 0 && item.tradeLockedUntil <= Emulator.getIntUnixTimestamp()) {
                            modToolSanctions.updateTradeLockedUntil(item.id, 0);
                            habbo.getHabboStats().setAllowTrade(true);
                        } else if (item.tradeLockedUntil > 0 && item.tradeLockedUntil > Emulator.getIntUnixTimestamp()) {
                            habbo.getHabboStats().setAllowTrade(false);
                        }

                        if (item.isMuted && item.muteDuration <= Emulator.getIntUnixTimestamp()) {
                            modToolSanctions.updateMuteDuration(item.id, 0);
                            habbo.unMute();
                        } else if (item.isMuted && item.muteDuration > Emulator.getIntUnixTimestamp()) {
                            Date muteDuration = new Date((long) item.muteDuration * 1000);
                            long diff = muteDuration.getTime() - Emulator.getDate().getTime();
                            habbo.mute(Math.toIntExact(diff), false);
                        }
                    }
                }

                Emulator.getPluginManager().fireEvent(new UserLoginEvent(habbo, this.client.getHabbo().getHabboInfo().getIpLogin()));

                if (Emulator.getConfig().getBoolean("hotel.welcome.alert.enabled")) {
                    final Habbo finalHabbo = habbo;
                    Emulator.getThreading().run(() -> {
                        if (Emulator.getConfig().getBoolean("hotel.welcome.alert.oldstyle")) {
                            SecureLoginEvent.this.client.sendResponse(new MessagesForYouComposer(HabboManager.WELCOME_MESSAGE.replace("%username%", finalHabbo.getHabboInfo().getUsername()).replace("%user%", finalHabbo.getHabboInfo().getUsername()).split("<br/>")));
                        } else {
                            SecureLoginEvent.this.client.sendResponse(new GenericAlertComposer(HabboManager.WELCOME_MESSAGE.replace("%username%", finalHabbo.getHabboInfo().getUsername()).replace("%user%", finalHabbo.getHabboInfo().getUsername())));
                        }
                    }, Emulator.getConfig().getInt("hotel.welcome.alert.delay", 5000));
                }

                if (SubscriptionHabboClub.HC_PAYDAY_ENABLED) {
                    SubscriptionHabboClub.processUnclaimed(habbo);
                }

                SubscriptionHabboClub.processClubBadge(habbo);

                Messenger.checkFriendSizeProgress(habbo);

                if (!habbo.getHabboStats().hasGottenDefaultSavedSearches) {
                    habbo.getHabboStats().hasGottenDefaultSavedSearches = true;
                    Emulator.getThreading().run(habbo.getHabboStats());

                    habbo.getHabboInfo().addSavedSearch(new NavigatorSavedSearch("official-root", ""));
                    habbo.getHabboInfo().addSavedSearch(new NavigatorSavedSearch("my", ""));
                    habbo.getHabboInfo().addSavedSearch(new NavigatorSavedSearch("favorites", ""));

                    this.client.sendResponse(new NewNavigatorSavedSearchesComposer(this.client.getHabbo().getHabboInfo().getSavedSearches()));
                }
            } else {
                Emulator.getGameServer().getGameClientManager().disposeClient(this.client);
                LOGGER.warn("Someone tried to login with a non-existing SSO token! Closed connection...");
            }
        } else {
            Emulator.getGameServer().getGameClientManager().disposeClient(this.client);
        }
    }
}
