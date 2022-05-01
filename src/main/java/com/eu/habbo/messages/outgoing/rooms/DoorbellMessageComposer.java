package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class DoorbellMessageComposer extends MessageComposer {
    private final String habbo;

    public DoorbellMessageComposer(String habbo) {
        this.habbo = habbo;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.doorbellMessageComposer);
        this.response.appendString(this.habbo);
        return this.response;
    }
}
