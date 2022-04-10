package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.threading.runnables.PetClearPosture;

public class ActionCroak extends PetAction {
    public ActionCroak() {
        super(PetTasks.SPEAK, false);
        this.minimumActionDuration = 2000;
    }

    @Override
    public boolean apply(Pet pet, Habbo habbo, String[] data) {
        pet.getRoomUnit().setStatus(RoomUnitStatus.CROAK, "0");

        Emulator.getThreading().run(new PetClearPosture(pet, RoomUnitStatus.CROAK, null, false), 2000);

        if (pet.getHappyness() > 80) {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.PLAYFUL));
            return true;
        } else if (pet.getHappyness() > 60 && pet.getHappyness() < 80) {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_HAPPY));
            return true;
        } else if (pet.getHappyness() > 40 && pet.getHappyness() < 60) {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_NEUTRAL));
            return true;
        } else if (pet.getHappyness() > 20 && pet.getHappyness() < 40) {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_SAD));
            return true;
        } else if (pet.getHappyness() < 20) {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.DISOBEY));
        }

        return false;
    }
}
