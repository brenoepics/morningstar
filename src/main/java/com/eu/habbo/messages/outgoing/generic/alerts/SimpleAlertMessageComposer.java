package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class SimpleAlertMessageComposer extends MessageComposer {

    String title;
    String message;

    public SimpleAlertMessageComposer(String message) {
        this.message = message;
    }

    public SimpleAlertMessageComposer(String message, String title) {
        this.message = message;
        this.title = title;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.SimpleAlertMessageComposer);
        this.response.appendString(message);
        if(title != null) {
            this.response.appendString(title);
        }
        return this.response;
    }
}
