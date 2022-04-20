package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.guilds.GuildRank;
import com.eu.habbo.habbohotel.guilds.GuildState;
import com.eu.habbo.habbohotel.guilds.forums.ForumThread;
import com.eu.habbo.habbohotel.guilds.forums.ForumThreadState;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumCommentsComposer;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumDataComposer;
import com.eu.habbo.messages.outgoing.handshake.ConnectionErrorComposer;



public class GuildForumThreadsMessagesEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int guildId = packet.readInt();
        int threadId = packet.readInt();
        int index = packet.readInt(); // 40
        int limit = packet.readInt(); // 20


        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);
        ForumThread thread = ForumThread.getById(threadId);
        boolean hasStaffPermissions = this.client.getHabbo().hasPermission(Permission.ACC_MODTOOL_TICKET_Q);
        if (guild == null || thread == null) {
            this.client.sendResponse(new ConnectionErrorComposer(404));
            return;
        }
        GuildMember member = Emulator.getGameEnvironment().getGuildManager().getGuildMember(guildId, this.client.getHabbo().getHabboInfo().getId());
        boolean isGuildAdministrator = (guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || member.getRank().equals(GuildRank.ADMIN));

        if (thread.getState() != ForumThreadState.HIDDEN_BY_GUILD_ADMIN || hasStaffPermissions || isGuildAdministrator) {
            this.client.sendResponse(new GuildForumCommentsComposer(guildId, threadId, index, thread.getComments(limit, index)));
            this.client.sendResponse(new GuildForumDataComposer(guild, this.client.getHabbo()));
        }
        else {
            this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FORUMS_ACCESS_DENIED.key).compose());
        }
    }
}