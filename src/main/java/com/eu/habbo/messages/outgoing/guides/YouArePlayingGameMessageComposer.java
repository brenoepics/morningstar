package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class YouArePlayingGameMessageComposer extends MessageComposer {
    public final boolean isPlaying;

    public YouArePlayingGameMessageComposer(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.youArePlayingGameMessageComposer);
        this.response.appendBoolean(this.isPlaying);
        return this.response;
    }
}