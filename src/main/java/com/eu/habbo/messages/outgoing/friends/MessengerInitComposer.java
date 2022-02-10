package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class MessengerInitComposer extends MessageComposer {
    private final Habbo habbo;

    public MessengerInitComposer(Habbo habbo) {
        this.habbo = habbo;
    }

    @Override
    protected ServerMessage composeInternal() {

        this.response.init(Outgoing.MessengerInitComposer);
        if (this.habbo.hasPermission("acc_infinite_friends")) {
            this.response.appendInt(Integer.MAX_VALUE);
            this.response.appendInt(1337);
            this.response.appendInt(Integer.MAX_VALUE);
        } else {
            this.response.appendInt(Messenger.MAXIMUM_FRIENDS);
            this.response.appendInt(1337);
            this.response.appendInt(Messenger.MAXIMUM_FRIENDS_HC);
        }
        if (this.habbo.getHabboInfo().getFriendListCategories() != "") {

            String[] friendcats = this.habbo.getHabboInfo().getFriendListCategories().split(";");
            this.response.appendInt(friendcats.length);
            int i = 0;
            for (String fc : friendcats) {
                this.response.appendInt(i++);
                this.response.appendString(fc);
            }
        } else {
            this.response.appendInt(0);
        }
        return this.response;
    }
}
