package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuideSessionRequesterRoomMessageComposer extends MessageComposer {
    private final Room room;

    public GuideSessionRequesterRoomMessageComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.guideSessionRequesterRoomMessageComposer);
        this.response.appendInt(this.room != null ? this.room.getId() : 0);
        return this.response;
    }
}
