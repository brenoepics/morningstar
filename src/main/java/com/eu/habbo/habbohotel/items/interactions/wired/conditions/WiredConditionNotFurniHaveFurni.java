package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredConditionOperator;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionNotFurniHaveFurni extends InteractionWiredCondition {
    public static final WiredConditionType type = WiredConditionType.NOT_FURNI_HAVE_FURNI;

    private boolean all;
    private THashSet<HabboItem> items;

    public WiredConditionNotFurniHaveFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.items = new THashSet<>();
    }

    public WiredConditionNotFurniHaveFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.items = new THashSet<>();
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        this.refresh();

        if (this.items.isEmpty())
            return true;

        if(this.all) {
            return this.items.stream().allMatch(item -> {
                double minZ = item.getZ() + Item.getCurrentHeight(item);
                THashSet<RoomTile> occupiedTiles = room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
                return occupiedTiles.stream().noneMatch(tile -> room.getItemsAt(tile).stream().anyMatch(matchedItem -> matchedItem != item && matchedItem.getZ() >= minZ));
            });
        }
        else {
            return this.items.stream().anyMatch(item -> {
                double minZ = item.getZ() + Item.getCurrentHeight(item);
                THashSet<RoomTile> occupiedTiles = room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
                return occupiedTiles.stream().noneMatch(tile -> room.getItemsAt(tile).stream().anyMatch(matchedItem -> matchedItem != item && matchedItem.getZ() >= minZ));
            });
        }
    }

    @Override
    public String getWiredData() {
        this.refresh();

        StringBuilder data = new StringBuilder((this.all ? "1" : "0") + ":");

        for (HabboItem item : this.items)
            data.append(item.getId()).append(";");

        return data.toString();
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        this.items.clear();

        String[] data = set.getString("wired_data").split(":");

        if (data.length >= 1) {
            this.all = (data[0].equals("1"));

            if (data.length == 2) {
                String[] items = data[1].split(";");

                for (String s : items) {
                    HabboItem item = room.getHabboItem(Integer.parseInt(s));

                    if (item != null)
                        this.items.add(item);
                }
            }
        }
    }

    @Override
    public void onPickUp() {
        this.all = false;
        this.items.clear();
    }

    @Override
    public WiredConditionType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        this.refresh();

        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.items.size());

        for (HabboItem item : this.items)
            message.appendInt(item.getId());

        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(1);
        message.appendInt(this.all ? 1 : 0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(0);
        message.appendInt(0);
    }

    @Override
    public boolean saveData(ClientMessage packet) {
        packet.readInt();

        this.all = packet.readInt() == 1;

        packet.readString();

        int count = packet.readInt();
        if (count > Emulator.getConfig().getInt("hotel.wired.furni.selection.count")) return false;

        this.items.clear();

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if (room != null) {
            for (int i = 0; i < count; i++) {
                HabboItem item = room.getHabboItem(packet.readInt());

                if (item != null)
                    this.items.add(item);
            }

            return true;
        }
        return false;
    }

    private void refresh() {
        THashSet<HabboItem> items = new THashSet<>();

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
        if (room == null) {
            items.addAll(this.items);
        } else {
            for (HabboItem item : this.items) {
                if (room.getHabboItem(item.getId()) == null)
                    items.add(item);
            }
        }

        for (HabboItem item : items) {
            this.items.remove(item);
        }
    }

    @Override
    public WiredConditionOperator operator() {
        // NICE TRY BUT THAT'S NOT HOW IT WORKS. NOTHING IN HABBO IS AN "OR" CONDITION - EVERY CONDITION MUST BE SUCCESS FOR THE STACK TO EXECUTE, BUT LET'S LEAVE IT IMPLEMENTED FOR PLUGINS TO USE.
        //return this.all ? WiredConditionOperator.AND : WiredConditionOperator.OR;
        return WiredConditionOperator.AND;
    }
}
