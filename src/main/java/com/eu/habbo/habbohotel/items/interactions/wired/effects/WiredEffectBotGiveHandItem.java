package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.threading.runnables.RoomUnitGiveHanditem;
import com.eu.habbo.threading.runnables.RoomUnitWalkToLocation;
import gnu.trove.procedure.TObjectProcedure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WiredEffectBotGiveHandItem extends InteractionWiredEffect {
    public static final WiredEffectType type = WiredEffectType.BOT_GIVE_HANDITEM;

    private String botName = "";
    private int itemId;

    public WiredEffectBotGiveHandItem(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectBotGiveHandItem(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        message.appendBoolean(false);
        message.appendInt(5);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString(this.botName);
        message.appendInt(1);
        message.appendInt(this.itemId);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());

        if (this.requiresTriggeringUser()) {
            List<Integer> invalidTriggers = new ArrayList<>();
            room.getRoomSpecialTypes().getTriggers(this.getX(), this.getY()).forEach(new TObjectProcedure<InteractionWiredTrigger>() {
                @Override
                public boolean execute(InteractionWiredTrigger object) {
                    if (!object.isTriggeredByRoomUnit()) {
                        invalidTriggers.add(object.getBaseItem().getSpriteId());
                    }
                    return true;
                }
            });
            message.appendInt(invalidTriggers.size());
            for (Integer i : invalidTriggers) {
                message.appendInt(i);
            }
        } else {
            message.appendInt(0);
        }
    }

    @Override
    public boolean saveData(ClientMessage packet, GameClient gameClient) throws WiredSaveException {
        packet.readInt();

        int itemId = packet.readInt();

        if(itemId < 0)
            itemId = 0;

        String botName = packet.readString();
        packet.readInt();
        int delay = packet.readInt();

        if(delay > Emulator.getConfig().getInt("hotel.wired.max_delay", 20))
            throw new WiredSaveException("Delay too long");

        this.itemId = itemId;
        this.botName = botName.substring(0, Math.min(botName.length(), Emulator.getConfig().getInt("hotel.wired.message.max_length", 100)));
        this.setDelay(delay);

        return true;
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        Habbo habbo = room.getHabbo(roomUnit);
        List<Bot> bots = room.getBots(this.botName);

        if (habbo != null && bots.size() == 1) {
            Bot bot = bots.get(0);

            List<Runnable> tasks = new ArrayList<>();
            tasks.add(new RoomUnitGiveHanditem(roomUnit, room, this.itemId));
            tasks.add(new RoomUnitGiveHanditem(bot.getRoomUnit(), room, 0));
            tasks.add(() -> {
                if(roomUnit.getRoom() != null && roomUnit.getRoom().getId() == room.getId() && roomUnit.getCurrentLocation().distance(bot.getRoomUnit().getCurrentLocation()) < 2) {
                    WiredHandler.handle(WiredTriggerType.BOT_REACHED_AVTR, bot.getRoomUnit(), room, new Object[]{});
                }
            });

            RoomTile tile = bot.getRoomUnit().getClosestAdjacentTile(roomUnit.getX(), roomUnit.getY(), true);

            if(tile != null) {
                bot.getRoomUnit().setGoalLocation(tile);
            }

            Emulator.getThreading().run(new RoomUnitGiveHanditem(bot.getRoomUnit(), room, this.itemId));
            Emulator.getThreading().run(new RoomUnitWalkToLocation(bot.getRoomUnit(), tile, room, tasks, tasks));

            return true;
        }

        return false;
    }

    @Override
    public String getWiredData() {
        return this.getDelay() + "" + ((char) 9) + "" + this.itemId + "" + ((char) 9) + "" + this.botName;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        String[] data = set.getString("wired_data").split(((char) 9) + "");

        if (data.length == 3) {
            this.setDelay(Integer.valueOf(data[0]));
            this.itemId = Integer.valueOf(data[1]);
            this.botName = data[2];
        }
    }

    @Override
    public void onPickUp() {
        this.botName = "";
        this.itemId = 0;
        this.setDelay(0);
    }

    @Override
    public boolean requiresTriggeringUser() {
        return true;
    }
}
