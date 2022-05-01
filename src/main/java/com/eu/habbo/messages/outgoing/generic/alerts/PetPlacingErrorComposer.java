package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PetPlacingErrorComposer extends MessageComposer {
    public static final int ROOM_ERROR_PETS_FORBIDDEN_IN_HOTEL = 0;
    public static final int ROOM_ERROR_PETS_FORBIDDEN_IN_FLAT = 1;
    public static final int ROOM_ERROR_MAX_PETS = 2;
    public static final int ROOM_ERROR_PETS_SELECTED_TILE_NOT_FREE = 3;
    public static final int ROOM_ERROR_PETS_NO_FREE_TILES = 4;
    public static final int ROOM_ERROR_MAX_OWN_PETS = 5;

    private final int errorCode;

    public PetPlacingErrorComposer(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.petPlacingErrorComposer);
        this.response.appendInt(this.errorCode);
        return this.response;
    }
}
