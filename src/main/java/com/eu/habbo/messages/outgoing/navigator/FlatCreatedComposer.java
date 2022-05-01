package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class FlatCreatedComposer extends MessageComposer {
    private final Room room;

    public FlatCreatedComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.flatCreatedComposer);
        this.response.appendInt(this.room.getId());
        this.response.appendString(this.room.getName());
        return this.response;
    }
}
