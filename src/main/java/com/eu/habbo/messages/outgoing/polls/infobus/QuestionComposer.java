package com.eu.habbo.messages.outgoing.polls.infobus;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class QuestionComposer extends MessageComposer {
    public final int duration;
    public final String question;

    public QuestionComposer(int duration, String question) {
        this.duration = duration;
        this.question = question;
    }
    //:test 3047 s:a i:10 i:20 i:10000 i:1 i:1 i:3 s:abcdefghijklmnopqrstuvwxyz12345678901234? i:1 s:a s:b

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.questionComposer);
        this.response.appendString(this.question);
        this.response.appendInt(0);
        this.response.appendInt(0);
        this.response.appendInt(this.duration); //duration
        this.response.appendInt(-1); //Id
        this.response.appendInt(0); //Number
        this.response.appendInt(3); //Type
        this.response.appendString(this.question);
        return this.response;
    }
}
