package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class AccountSafetyLockStatusChangeMessageComposer extends MessageComposer {
    public final int STATUS_ZERO = 0;
    public final int STATUS_ONE = 1;

    private final int status;

    public AccountSafetyLockStatusChangeMessageComposer(int status) {
        this.status = status;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.accountSafetyLockStatusChangeMessageComposer);
        this.response.appendInt(this.status);
        return this.response;
    }
}