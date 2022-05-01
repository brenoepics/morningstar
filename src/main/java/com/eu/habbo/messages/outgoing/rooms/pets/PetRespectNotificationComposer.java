package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PetRespectNotificationComposer extends MessageComposer {
    public final static int PET_RESPECTED = 1;
    public final static int PET_TREATED = 2;

    private final Pet pet;
    private final int type;

    public PetRespectNotificationComposer(Pet pet) {
        this.pet = pet;
        this.type = 1;
    }

    public PetRespectNotificationComposer(Pet pet, int type) {
        this.pet = pet;
        this.type = type;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.petRespectNotificationComposer);
        this.response.appendInt(this.type);
        this.response.appendInt(100);
        this.pet.serialize(this.response);
        return this.response;
    }
}
