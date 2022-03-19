package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class YouAreSpectatorMessage extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.RoomUnknown3Composer);
        //Empty body
        return this.response;
    }
}