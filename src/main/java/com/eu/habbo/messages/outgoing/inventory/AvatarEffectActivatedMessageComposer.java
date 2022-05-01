package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.users.inventory.EffectsComponent;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class AvatarEffectActivatedMessageComposer extends MessageComposer {
    public final EffectsComponent.HabboEffect effect;

    public AvatarEffectActivatedMessageComposer(EffectsComponent.HabboEffect effect) {
        this.effect = effect;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.avatarEffectActivatedMessageComposer);
        this.response.appendInt(this.effect.effect); //Type
        this.response.appendInt(this.effect.duration); //Duration
        this.response.appendBoolean(this.effect.enabled); //activated
        return this.response;
    }
}