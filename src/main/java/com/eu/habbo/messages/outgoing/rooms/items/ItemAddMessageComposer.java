package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class ItemAddMessageComposer extends MessageComposer {
    private final HabboItem item;
    private final String itemOwnerName;

    public ItemAddMessageComposer(HabboItem item, String itemOwnerName) {
        this.item = item;
        this.itemOwnerName = itemOwnerName;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.itemAddMessageComposer);
        this.item.serializeWallData(this.response);
        this.response.appendString(this.itemOwnerName);
        return this.response;
    }
}
