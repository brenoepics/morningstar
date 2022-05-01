package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomFilterSettingsMessageComposer extends MessageComposer {
    private final Room room;

    public RoomFilterSettingsMessageComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roomFilterSettingsMessageComposer);

        this.response.appendInt(this.room.getWordFilterWords().size());

        for (String string : this.room.getWordFilterWords()) {
            this.response.appendString(string);
        }

        return this.response;
    }
}
