package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

public class UserEffectEvent extends UserEvent {
    private int effectID;
    private int duration;

    public UserEffectEvent(Habbo habbo) {
        super(habbo);
    }

    public int getEffectID() {
        return effectID;
    }

    public int getDuration() {
        return duration;
    }

    public void setEffectID(int effectID) {
        this.effectID = effectID;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
