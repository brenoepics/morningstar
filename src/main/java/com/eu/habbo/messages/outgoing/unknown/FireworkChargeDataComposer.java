package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class FireworkChargeDataComposer extends MessageComposer {

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.FireworkChargeDataComposer);
        this.response.appendInt(1); //stuffId
        this.response.appendInt(2); //charges
        this.response.appendInt(3);
        this.response.appendInt(4);
        this.response.appendInt(5);
        this.response.appendInt(6);
        return this.response;
    }
}