package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

public class UserMuteEvent extends UserEvent{
    private int seconds;

    public UserMuteEvent(Habbo habbo) {
        super(habbo);
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}
