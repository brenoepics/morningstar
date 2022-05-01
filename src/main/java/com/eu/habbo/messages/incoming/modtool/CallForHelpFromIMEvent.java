package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolChatLog;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.habbohotel.modtool.ModToolTicketType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.CallForHelpDisabledNotifyMessageComposer;
import com.eu.habbo.threading.runnables.InsertModToolIssue;

import java.util.ArrayList;

public class CallForHelpFromIMEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        if (!this.client.getHabbo().getHabboStats().allowTalk()) {
            this.client.sendResponse(new CallForHelpDisabledNotifyMessageComposer());
            return;
        }

        String message = this.packet.readString();
        int category = this.packet.readInt();
        int userId = this.packet.readInt();
        int count = this.packet.readInt();
        ArrayList<ModToolChatLog> chatLogs = new ArrayList<>();

        HabboInfo info;
        Habbo target = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);
        if (target != null) {
            info = target.getHabboInfo();
        } else {
            info = HabboManager.getOfflineHabboInfo(userId);
        }

        if (info == null) return;
        for (int i = 0; i < count; i++) {
            int chatUserId = this.packet.readInt();
            String username = this.packet.readInt() == info.getId() ? info.getUsername() : this.client.getHabbo().getHabboInfo().getUsername();

            chatLogs.add(new ModToolChatLog(0, chatUserId, username, this.packet.readString()));
        }


        ModToolIssue issue = new ModToolIssue(this.client.getHabbo().getHabboInfo().getId(), this.client.getHabbo().getHabboInfo().getUsername(), userId, info.getUsername(), 0, message, ModToolTicketType.IM);
        issue.category = category;
        issue.chatLogs = chatLogs;
        new InsertModToolIssue(issue).run();
        Emulator.getGameEnvironment().getModToolManager().addTicket(issue);
        Emulator.getGameEnvironment().getModToolManager().updateTicketToMods(issue);
    }
}
