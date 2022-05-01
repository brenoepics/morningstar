package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.habbohotel.rooms.RoomCategory;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.List;

public class CategoriesWithVisitorCountComposer extends MessageComposer {
    public final List<RoomCategory> roomCategories;

    public CategoriesWithVisitorCountComposer(List<RoomCategory> roomCategories) {
        this.roomCategories = roomCategories;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.categoriesWithVisitorCountComposer);
        this.response.appendInt(this.roomCategories.size());

        for (RoomCategory category : this.roomCategories) {
            this.response.appendInt(0);
            this.response.appendInt(0);
            this.response.appendInt(200);
        }
        return this.response;
    }
}