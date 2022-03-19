package com.eu.habbo.messages.outgoing.polls.infobus;

import com.eu.habbo.habbohotel.polls.infobus.PollChoice;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.ArrayList;

public class RoomPollResultComposer extends MessageComposer {

    public final String question;
    public final ArrayList<PollChoice> choices;
    public final Integer total;

    public RoomPollResultComposer(String question, ArrayList<PollChoice> choices, Integer total) {
        this.question = question;
        this.choices = choices;
        this.total = total;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.RoomPollResultComposer);
        this.response.appendString(this.question);
        this.response.appendInt(this.choices.size());
        for(PollChoice choice : this.choices) {
            this.response.appendInt(choice.getId());
            this.response.appendString(choice.getName());
            this.response.appendInt(choice.getVotes());
        }
        this.response.appendInt(total);
        return this.response;
    }
}