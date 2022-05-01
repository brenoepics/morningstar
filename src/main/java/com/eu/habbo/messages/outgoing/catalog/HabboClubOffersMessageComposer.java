package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.ClubOffer;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.List;

public class HabboClubOffersMessageComposer extends MessageComposer {
    private final int windowId;
    private final Habbo habbo;

    public HabboClubOffersMessageComposer(Habbo habbo, int windowId) {
        this.habbo = habbo;
        this.windowId = windowId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.habboClubOffersMessageComposer);

        List<ClubOffer> offers = Emulator.getGameEnvironment().getCatalogManager().getClubOffers();
        this.response.appendInt(offers.size());

        //TODO Change this to a seperate table.
        for (ClubOffer offer : offers) {
            offer.serialize(this.response, this.habbo.getHabboStats().getClubExpireTimestamp());
        }

        this.response.appendInt(this.windowId);
        return this.response;
    }
}
