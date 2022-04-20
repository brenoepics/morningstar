package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionEffectVendingMachineNoSides extends InteractionVendingMachine {
    public InteractionEffectVendingMachineNoSides(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.setExtradata("0");
    }

    public InteractionEffectVendingMachineNoSides(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.setExtradata("0");
    }

    @Override
    public void giveVendingMachineItem(Room room, RoomUnit unit) {
        room.giveEffect(unit, this.getBaseItem().getRandomVendingItem(), 30);
    }

    @Override
    public THashSet<RoomTile> getActivatorTiles(Room room) {

        THashSet<RoomTile> tiles = new THashSet<RoomTile>();
        for(int x = -1; x <= 1; x++) {
            for(int y = -1; y <= 1; y++) {
                RoomTile tile = room.getLayout().getTile((short)(this.getX() + x), (short)(this.getY() + y));
                if(tile != null) {
                    tiles.add(tile);
                }
            }
        }

        return tiles;
    }
}