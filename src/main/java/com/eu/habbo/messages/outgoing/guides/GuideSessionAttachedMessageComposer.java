package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guides.GuideTour;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuideSessionAttachedMessageComposer extends MessageComposer {
    private final GuideTour tour;
    private final boolean isHelper;

    public GuideSessionAttachedMessageComposer(GuideTour tour, boolean isHelper) {
        this.tour = tour;
        this.isHelper = isHelper;
    }

    @Override
    protected ServerMessage composeInternal() {
        //:test 3549 b:1 i:1 s:abcd i:100
        this.response.init(Outgoing.guideSessionAttachedMessageComposer);
        this.response.appendBoolean(this.isHelper); //? //isHelper
        this.response.appendInt(1);       //? Tour type
        this.response.appendString(this.tour.getHelpRequest());    //? Instruction (Help message)
        this.response.appendInt(this.isHelper ? 60 : Emulator.getGameEnvironment().getGuideManager().getAverageWaitingTime());       //? Avarage Waiting Time (for noob) | Time left to pickup (For helper)
        return this.response;
    }
}
