package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.Map;

public class AcceptFriendResultComposer extends MessageComposer {
    private final Map<Integer, Integer> errors;

    public AcceptFriendResultComposer(Map<Integer, Integer> errors) {
        this.errors = errors;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.acceptFriendResultComposer);
        this.response.appendInt(this.errors.size());
        for (Map.Entry<Integer, Integer> entry : this.errors.entrySet()) {
            this.response.appendInt(entry.getKey());
            this.response.appendInt(entry.getValue());
        }
        return this.response;
    }
}