package com.eu.habbo.plugin.events.furniture.wired;

import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.plugin.events.roomunit.RoomUnitEvent;

public class WiredConditionFailedEvent extends RoomUnitEvent {

    public final InteractionWiredTrigger trigger;

    public final InteractionWiredEffect effect;
    
    public final InteractionWiredCondition condition;


    public WiredConditionFailedEvent(Room room, RoomUnit roomUnit, InteractionWiredTrigger trigger, InteractionWiredEffect effect, InteractionWiredCondition condition) {
        super(room, roomUnit);
        this.effect = effect;
        this.trigger = trigger;
        this.condition = condition;
    }
}
