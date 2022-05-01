package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.procedure.TIntProcedure;

public class FavouriteChangedComposer extends MessageComposer {
    private final Habbo habbo;

    public FavouriteChangedComposer(Habbo habbo) {
        this.habbo = habbo;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.favouritesComposer);
        this.response.appendInt(Emulator.getConfig().getInt("hotel.rooms.max.favorite"));
        this.response.appendInt(this.habbo.getHabboStats().getFavoriteRooms().size());
        this.habbo.getHabboStats().getFavoriteRooms().forEach(new TIntProcedure() {
            @Override
            public boolean execute(int value) {
                FavouriteChangedComposer.this.response.appendInt(value);
                return true;
            }
        });
        return this.response;
    }
}
