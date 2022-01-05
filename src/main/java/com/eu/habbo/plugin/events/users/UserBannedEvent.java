package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.modtool.ModToolBan;
import com.eu.habbo.habbohotel.modtool.ModToolBanType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.plugin.Event;

public class UserBannedEvent extends Event {
    private Habbo executor;
    private HabboInfo target;
    private ModToolBan modToolBan;

    public UserBannedEvent(Habbo executor, HabboInfo target, ModToolBan modToolBan){
        this.executor = executor;
        this.target = target;
        this.modToolBan = modToolBan;
    }
    public Habbo getExecutor() {
        return executor;
    }

    public HabboInfo getTarget() {
        return target;
    }

    public ModToolBan getModToolBan() {
        return modToolBan;
    }
}
