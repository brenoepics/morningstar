package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class MysteryBoxKeysMessageComposer extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.MysteryBoxKeysMessageComposer);
        this.response.appendString(""); //Box color
        this.response.appendString(""); //Key color
        return this.response;
    }
}