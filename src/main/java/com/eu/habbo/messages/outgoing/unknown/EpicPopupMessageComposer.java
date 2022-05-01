package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class EpicPopupMessageComposer extends MessageComposer {
    public static final String LIBRARY_URL = "${image.library.url}";
    private final String assetURI;

    public EpicPopupMessageComposer(String assetURI) {
        this.assetURI = assetURI;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.epicPopupMessageComposer);
        this.response.appendString(this.assetURI);
        return this.response;
    }
}
