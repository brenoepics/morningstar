package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class FigureUpdateComposer extends MessageComposer {
    private final Habbo habbo;

    public FigureUpdateComposer(Habbo habbo) {
        this.habbo = habbo;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.figureUpdateComposer);
        this.response.appendString(this.habbo.getHabboInfo().getLook());
        this.response.appendString(this.habbo.getHabboInfo().getGender().name());
        return this.response;
    }
}
