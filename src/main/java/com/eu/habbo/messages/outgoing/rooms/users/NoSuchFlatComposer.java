package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class NoSuchFlatComposer extends MessageComposer {
    private final Room room;
    private final int habboId;

    public NoSuchFlatComposer(Room room, int habboId) {
        this.room = room;
        this.habboId = habboId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.noSuchFlatComposer);
        this.response.appendInt(this.room.getId());
        this.response.appendInt(this.habboId);
        return this.response;
    }
}
