package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionNotInTeam extends InteractionWiredCondition {
    public static final WiredConditionType type = WiredConditionType.NOT_ACTOR_IN_TEAM;

    private GameTeamColors teamColor = GameTeamColors.RED;

    public WiredConditionNotInTeam(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionNotInTeam(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo != null) {
            return habbo.getHabboInfo().getGamePlayer() == null || !habbo.getHabboInfo().getGamePlayer().getTeamColor().equals(this.teamColor); // user is not part of any team
        }

        return true;
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(
                this.teamColor
        ));
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        try {
            String wiredData = set.getString("wired_data");

            if (wiredData.startsWith("{")) {
                JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
                this.teamColor = data.teamColor;
            } else {
                if (!wiredData.equals(""))
                    this.teamColor = GameTeamColors.values()[Integer.parseInt(wiredData)];
            }
        } catch (Exception e) {
            this.teamColor = GameTeamColors.RED;
        }
    }

    @Override
    public void onPickUp() {
        this.teamColor = GameTeamColors.RED;
    }

    @Override
    public WiredConditionType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        message.appendBoolean(false);
        message.appendInt(5);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(1);
        message.appendInt(this.teamColor.type);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(0);
        message.appendInt(0);
    }

    @Override
    public boolean saveData(ClientMessage packet) {
        packet.readInt();

        this.teamColor = GameTeamColors.values()[packet.readInt()];

        return true;
    }

    static class JsonData {
        GameTeamColors teamColor;

        public JsonData(GameTeamColors teamColor) {
            this.teamColor = teamColor;
        }
    }
}
