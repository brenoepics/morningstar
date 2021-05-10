package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

public class UserCommandEvent extends UserEvent {

    public final String[] args;


    public final boolean success;


    public UserCommandEvent(Habbo habbo, String[] args, boolean success) {
        super(habbo);
        this.args = args;
        this.success = success;
    }
}
