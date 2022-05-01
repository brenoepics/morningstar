package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PurchaseErrorMessageComposer extends MessageComposer {
    public static final int SERVER_ERROR = 0;
    public static final int ALREADY_HAVE_BADGE = 1;

    private final int error;

    public PurchaseErrorMessageComposer(int error) {
        this.error = error;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.purchaseErrorMessageComposer);
        this.response.appendInt(this.error);
        return this.response;
    }
}
