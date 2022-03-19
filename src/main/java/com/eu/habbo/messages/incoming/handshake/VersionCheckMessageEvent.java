package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.messages.NoAuthMessage;
import com.eu.habbo.messages.incoming.MessageHandler;

@NoAuthMessage
public class VersionCheckMessageEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        Integer clientID = this.packet.readInt();
        String clientURL = this.packet.readString();
        String externalVariablesURL = this.packet.readString();
    }
}
