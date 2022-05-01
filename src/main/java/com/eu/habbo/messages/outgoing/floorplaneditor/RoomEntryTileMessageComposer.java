package com.eu.habbo.messages.outgoing.floorplaneditor;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomEntryTileMessageComposer extends MessageComposer {
    private final Room room;

    public RoomEntryTileMessageComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roomEntryTileMessageComposer);
        this.response.appendInt(this.room.getLayout().getDoorX());
        this.response.appendInt(this.room.getLayout().getDoorY());
        this.response.appendInt(this.room.getLayout().getDoorDirection());
        return this.response;
    }
}
