package com.eu.habbo.messages.outgoing.habboway.nux;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class InClientLinkMessageComposer extends MessageComposer {
    private final String link;

    public InClientLinkMessageComposer(String link) {
        this.link = link;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.inClientLinkMessageComposer);
        this.response.appendString(this.link);
        return this.response;
    }
}