package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class DanceMessageComposer extends MessageComposer {
    private final RoomUnit roomUnit;

    public DanceMessageComposer(RoomUnit roomUnit) {
        this.roomUnit = roomUnit;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.danceMessageComposer);
        this.response.appendInt(this.roomUnit.getId());
        this.response.appendInt(this.roomUnit.getDanceType().getType());
        return this.response;
    }
}
