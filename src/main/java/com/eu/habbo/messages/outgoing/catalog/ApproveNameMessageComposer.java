package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class ApproveNameMessageComposer extends MessageComposer {
    public static final int NAME_OK = 0;
    public static final int NAME_TO_LONG = 1;
    public static final int NAME_TO_SHORT = 2;
    public static final int FORBIDDEN_CHAR = 3;
    public static final int FORBIDDEN_WORDS = 4;

    private final int type;
    private final String value;

    public ApproveNameMessageComposer(int type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.approveNameMessageComposer);
        this.response.appendInt(this.type);
        this.response.appendString(this.value);
        return this.response;
    }
}
