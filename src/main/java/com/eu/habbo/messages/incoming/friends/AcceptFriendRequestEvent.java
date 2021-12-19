package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.FriendRequestErrorComposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.eu.habbo.habbohotel.users.HabboManager.getOfflineHabboInfo;

public class AcceptFriendRequestEvent extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptFriendRequestEvent.class);

    @Override
    public void handle() throws Exception {
        int count = this.packet.readInt();
        int userId;

        for (int i = 0; i < count; i++) {
            userId = this.packet.readInt();

            if (userId == 0)
                return;

            if (this.client.getHabbo().getMessenger().getFriends().containsKey(userId)) {
                this.client.getHabbo().getMessenger().deleteFriendRequests(userId, this.client.getHabbo().getHabboInfo().getId());
                continue;
            }

            Habbo target = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);

            if(target == null) {
                HabboInfo habboInfo = getOfflineHabboInfo(userId);

                if(habboInfo == null) {
                    this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.TARGET_NOT_FOUND));
                    this.client.getHabbo().getMessenger().deleteFriendRequests(userId, this.client.getHabbo().getHabboInfo().getId());
                    continue;
                }

                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT users.*, users_settings.block_friendrequests FROM users INNER JOIN users_settings ON users.id = users_settings.user_id WHERE username = ? LIMIT 1")) {
                    statement.setString(1, habboInfo.getUsername());
                    try (ResultSet set = statement.executeQuery()) {
                        while (set.next()) {
                            target = new Habbo(set);
                        }
                    }
                } catch (SQLException e) {
                    LOGGER.error("Caught SQL exception", e);
                    return;
                }
            }

            if(target == null) {
                this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.TARGET_NOT_FOUND));
                this.client.getHabbo().getMessenger().deleteFriendRequests(userId, this.client.getHabbo().getHabboInfo().getId());
                continue;
            }

            if(this.client.getHabbo().getMessenger().getFriends().size() >= this.client.getHabbo().getHabboStats().maxFriends && !this.client.getHabbo().hasPermission("acc_infinite_friends")) {
                this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.FRIEND_LIST_OWN_FULL));
                break;
            }

            if(target.getMessenger().getFriends().size() >= target.getHabboStats().maxFriends && !target.hasPermission("acc_infinite_friends")) {
                this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.FRIEND_LIST_TARGET_FULL));
                continue;
            }

            this.client.getHabbo().getMessenger().acceptFriendRequest(userId, this.client.getHabbo().getHabboInfo().getId());

            Messenger.checkFriendSizeProgress(this.client.getHabbo());
            Messenger.checkFriendSizeProgress(target);
        }
    }
}
