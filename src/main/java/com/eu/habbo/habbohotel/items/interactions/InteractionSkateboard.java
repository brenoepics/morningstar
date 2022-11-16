package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionSkateboard extends InteractionDefault 
{
    public InteractionSkateboard(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public InteractionSkateboard(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        room.giveEffect(roomUnit, 71, -1);

        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo == null) return;

        if (habbo.getRoomUnit().tilesWalked() % 3 == 0)
        {
            AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("SkateBoardJump"));   
        }

        AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("SkateBoardSlide"));
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOff(roomUnit, room, objects);

        if (roomUnit.getEffectId() == 71)
        {
            room.giveEffect(roomUnit, 0, -1);
        }
    }

    @Override
    public void onPlace(Room room)
    {
        super.onPlace(room);

        Habbo itemOwner = Emulator.getGameEnvironment().getHabboManager().getHabbo(this.getUserId());

        if (itemOwner != null) {
            AchievementManager.progressAchievement(itemOwner, Emulator.getGameEnvironment().getAchievementManager().getAchievement("skateBoardBuild"));
        }
    }

    @Override
    public void onPickUp(Room room)
    {
        for (Habbo habbo : room.getHabbosOnItem(this))
        {
            if (habbo.getRoomUnit().getEffectId() == 71)
            {
                room.giveEffect(habbo, 0, -1);
            }
        }
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation)
    {
        Rectangle newRect = RoomLayout.getRectangle(newLocation.x, newLocation.y, this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation());

        for (Habbo habbo : room.getHabbosOnItem(this))
        {
            if (habbo.getRoomUnit().getEffectId() == 71 && !newRect.contains(habbo.getRoomUnit().getCurrentLocation().x, habbo.getRoomUnit().getCurrentLocation().y))
            {
                room.giveEffect(habbo, 0, -1);
            }
        }
    }
}
