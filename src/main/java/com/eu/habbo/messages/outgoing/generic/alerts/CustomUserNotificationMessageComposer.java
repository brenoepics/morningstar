package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class CustomUserNotificationMessageComposer extends MessageComposer {
    public static final int HOPPER_NO_COSTUME = 1;
    public static final int HOPPER_NO_HC = 2;
    public static final int GATE_NO_HC = 3;
    public static final int STARS_NOT_CANDIDATE = 4;
    public static final int STARS_NOT_ENOUGH_USERS = 5;

    private final int type;

    public CustomUserNotificationMessageComposer(int type) {
        this.type = type;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.customUserNotificationMessageComposer);
        this.response.appendInt(this.type);
        return this.response;
    }
}
