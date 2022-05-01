package com.eu.habbo.messages.outgoing.floorplaneditor;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

public class RoomOccupiedTilesMessageComposer extends MessageComposer {
    private final Room room;

    public RoomOccupiedTilesMessageComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roomOccupiedTilesMessageComposer);

        THashSet<RoomTile> tileList = this.room.getLockedTiles();

        this.response.appendInt(tileList.size());
        for (RoomTile node : tileList) {
            this.response.appendInt((int) node.x);
            this.response.appendInt((int) node.y);
        }

        return this.response;
    }
}
