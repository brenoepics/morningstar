package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.plugin.events.users.UserKickEvent;

public class RoomUserKickEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room == null)
            return;

        int userId = this.packet.readInt();

        Habbo target = room.getHabbo(userId);

        if (target == null)
            return;

        /*
            0 - No one
            1 - Users with rights
            2 - All
         */

        if (room.hasRights(target) && target == this.client.getHabbo())
            return;

        if (target.hasPermission(Permission.ACC_UNKICKABLE)) {
            this.client.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_kick.unkickable").replace("%username%", target.getHabboInfo().getUsername()), this.client.getHabbo(), this.client.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return;
        }

        if (room.isOwner(target)) {
            return;
        }

        if (room.getKickOption() == 0 && !(room.isOwner(this.client.getHabbo()) || this.client.getHabbo().hasPermission(Permission.ACC_ANYROOMOWNER) || this.client.getHabbo().hasPermission(Permission.ACC_AMBASSADOR))) {
            return;
        }

        if (room.hasRights(target) && room.hasRights(this.client.getHabbo()) && room.getKickOption() == 1 && !(room.isOwner(this.client.getHabbo()) || this.client.getHabbo().hasPermission(Permission.ACC_ANYROOMOWNER) || this.client.getHabbo().hasPermission(Permission.ACC_AMBASSADOR)))
            return;

        if (room.hasRights(target) && room.hasRights(this.client.getHabbo()) && room.getKickOption() != 2 && !(room.isOwner(this.client.getHabbo()) || this.client.getHabbo().hasPermission(Permission.ACC_ANYROOMOWNER) || this.client.getHabbo().hasPermission(Permission.ACC_AMBASSADOR)))
            return;

        if (!room.hasRights(this.client.getHabbo()) && room.getKickOption() == 2 && room.hasRights(target) && !(room.isOwner(this.client.getHabbo()) || this.client.getHabbo().hasPermission(Permission.ACC_ANYROOMOWNER) || this.client.getHabbo().hasPermission(Permission.ACC_AMBASSADOR)))
            return;

        UserKickEvent event = new UserKickEvent(this.client.getHabbo(), target);
        Emulator.getPluginManager().fireEvent(event);

        if (event.isCancelled())
            return;

        if (room.hasRights(this.client.getHabbo()) || room.getKickOption() == 2 || this.client.getHabbo().hasPermission(Permission.ACC_ANYROOMOWNER) || this.client.getHabbo().hasPermission(Permission.ACC_AMBASSADOR)) {
            room.kickHabbo(target, true);
            AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("SelfModKickSeen"));
        }
    }
}
