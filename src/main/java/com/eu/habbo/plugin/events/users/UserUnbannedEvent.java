package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.plugin.Event;

public class UserUnbannedEvent extends Event {
    private Habbo executor;
    private String target;

    public UserUnbannedEvent(Habbo executor, String target){
        this.executor = executor;
        this.target = target;
    }

    public Habbo getExecutor() {
        return executor;
    }

    public String getTarget() {
        return target;
    }
}
