package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserTagsComposer;

public class RequestRoomUserTagsEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        int habboId = this.packet.readInt();

        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != null) {
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(habboId);
                this.client.sendResponse(new RoomUserTagsComposer(habbo));
        }

    }
}
