package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PresentOpenedMessageComposer extends MessageComposer {
    private final HabboItem item;
    private final String text;
    private final boolean unknown;

    public PresentOpenedMessageComposer(HabboItem item, String text, boolean unknown) {
        this.item = item;
        this.text = text;
        this.unknown = unknown;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.presentOpenedMessageComposer);
        this.response.appendString(this.item.getBaseItem().getType().code.toLowerCase());
        this.response.appendInt(this.item.getBaseItem().getSpriteId());
        this.response.appendString(this.item.getBaseItem().getName());
        this.response.appendInt(this.item.getId());
        this.response.appendString(this.item.getBaseItem().getType().code.toLowerCase());
        this.response.appendBoolean(this.unknown);
        this.response.appendString(this.text);
        return this.response;
    }
}
