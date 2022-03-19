package com.eu.habbo.messages.incoming.polls.infobus;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;

public class VotePollCounterMessageEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        int counter = this.packet.readInt();
        Room room = this.client.getHabbo().getRoomUnit().getRoom();
        if(room != null) {
            room.handleInfobusPoll(this.client.getHabbo(), counter);
        }
    }
}
