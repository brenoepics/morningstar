package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class MaintenanceStatusMessageComposer extends MessageComposer {
    private final int closeInMinutes;
    private final int reopenInMinutes;

    public MaintenanceStatusMessageComposer(int closeInMinutes, int reopenInMinutes) {
        this.closeInMinutes = closeInMinutes;
        this.reopenInMinutes = reopenInMinutes;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.maintenanceStatusMessageComposer);
        this.response.appendBoolean(true);
        this.response.appendInt(this.closeInMinutes);
        this.response.appendInt(this.reopenInMinutes);
        return this.response;
    }
}
