package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUserAction;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserActionComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserDanceComposer;
import com.eu.habbo.plugin.events.users.UserIdleEvent;

public class RoomUserActionEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
        if (room != null) {
            Habbo habbo = this.client.getHabbo();

            if (this.client.getHabbo().getRoomUnit().getCacheable().get("control") != null) {
                habbo = (Habbo) this.client.getHabbo().getRoomUnit().getCacheable().get("control");

                if (habbo.getHabboInfo().getCurrentRoom() != room) {
                    habbo.getRoomUnit().getCacheable().remove("controller");
                    this.client.getHabbo().getRoomUnit().getCacheable().remove("control");
                    habbo = this.client.getHabbo();
                }
            }

            int action = this.packet.readInt();

            //Check if the action is ' wave ', the setting is enabled and if is dancing
            if (action == 1 && Emulator.getConfig().getBoolean("hotel.user_action.wave.stop_dance", false) && habbo.getRoomUnit().getDanceType() != DanceType.NONE) {
                habbo.getRoomUnit().setDanceType(DanceType.NONE);
                habbo.getHabboInfo().getCurrentRoom().sendComposer(new RoomUserDanceComposer(habbo.getRoomUnit()).compose());
            }

            if (action == 5) {
                UserIdleEvent event = new UserIdleEvent(this.client.getHabbo(), UserIdleEvent.IdleReason.ACTION, true);
                Emulator.getPluginManager().fireEvent(event);

                if (!event.isCancelled()) {
                    if (event.idle) {
                        room.idle(habbo);
                    } else {
                        room.unIdle(habbo);
                    }
                }
            } else {
                UserIdleEvent event = new UserIdleEvent(this.client.getHabbo(), UserIdleEvent.IdleReason.ACTION, false);
                Emulator.getPluginManager().fireEvent(event);

                if (!event.isCancelled()) {
                    if (!event.idle) {
                        room.unIdle(habbo);
                    }
                }

            }

            room.sendComposer(new RoomUserActionComposer(habbo.getRoomUnit(), RoomUserAction.fromValue(action)).compose());
        }
    }
}
