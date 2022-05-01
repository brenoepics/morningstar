package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuideSessionInvitedToGuideRoomMessageComposer extends MessageComposer {
    private final Room room;

    public GuideSessionInvitedToGuideRoomMessageComposer(Room room) {
        this.room = room;
    }

    //Helper invites noob
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.guideSessionInvitedToGuideRoomMessageComposer);
        this.response.appendInt(this.room != null ? this.room.getId() : 0);
        this.response.appendString(this.room != null ? this.room.getName() : "");
        return this.response;
    }
}
