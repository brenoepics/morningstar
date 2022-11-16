package com.eu.habbo.habbohotel.games.battlebanzai;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameGate;
import com.eu.habbo.habbohotel.rooms.Room;

public class BattleBanzaiGameTeam extends GameTeam {
    public BattleBanzaiGameTeam(GameTeamColors teamColor) {
        super(teamColor);
    }

    @Override
    public void addMember(GamePlayer gamePlayer) {

        gamePlayer.getHabbo().getHabboStats().setOldEffectId(gamePlayer.getHabbo().getRoomUnit().getEffectId());

        super.addMember(gamePlayer);

        gamePlayer.getHabbo().getHabboInfo().getCurrentRoom().giveEffect(gamePlayer.getHabbo(), BattleBanzaiGame.effectId + this.teamColor.type, -1);
    }

    @Override
    public void removeMember(GamePlayer gamePlayer) {
        Game game = gamePlayer.getHabbo().getHabboInfo().getCurrentRoom().getGame(gamePlayer.getHabbo().getHabboInfo().getCurrentGame());
        Room room = gamePlayer.getHabbo().getRoomUnit().getRoom();

        if (Emulator.getGameEnvironment().getItemManager().isFurniEffect(gamePlayer.getHabbo().getHabboStats().getOldEffectId())) {
            room.giveEffect(gamePlayer.getHabbo(), 0, -1);
        } else {
            room.giveEffect(gamePlayer.getHabbo(), gamePlayer.getHabbo().getHabboStats().getOldEffectId(), -1);
        }

        gamePlayer.getHabbo().getRoomUnit().setCanWalk(true);

        super.removeMember(gamePlayer);

        if (room != null && room.getRoomSpecialTypes() != null) {
            for (InteractionGameGate gate : room.getRoomSpecialTypes().getBattleBanzaiGates().values()) {
                gate.updateState(game, 5);
            }
        }
    }
}
