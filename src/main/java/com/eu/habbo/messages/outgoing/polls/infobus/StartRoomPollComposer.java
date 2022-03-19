package com.eu.habbo.messages.outgoing.polls.infobus;

import com.eu.habbo.habbohotel.polls.infobus.PollChoice;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.ArrayList;

public class StartRoomPollComposer extends MessageComposer {

    public final String question;
    public final ArrayList<PollChoice> choices;

    public StartRoomPollComposer(String question, ArrayList<PollChoice> choices) {
        this.question = question;
        this.choices = choices;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.StartRoomPollComposer);
        this.response.appendString(this.question);
        this.response.appendInt(this.choices.size());

        for(PollChoice choice : this.choices) {
            this.response.appendInt(choice.getId());
            this.response.appendString(choice.getName());
        }

        return this.response;
    }
}