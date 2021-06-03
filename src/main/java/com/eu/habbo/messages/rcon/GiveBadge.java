package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.messages.outgoing.users.AddUserBadgeComposer;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class GiveBadge extends RCONMessage<GiveBadge.GiveBadgeJSON> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GiveBadge.class);


    public GiveBadge() {
        super(GiveBadgeJSON.class);
    }

    @Override
    public void handle(Gson gson, GiveBadgeJSON json) {
        if (json.user_id == -1) {
            this.status = RCONMessage.HABBO_NOT_FOUND;
            return;
        }

        if (json.badge.isEmpty()) {
            this.status = RCONMessage.SYSTEM_ERROR;
            return;
        }

        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(json.user_id);

        String username = json.user_id + "";
        if (habbo != null) {
            username = habbo.getHabboInfo().getUsername();

            for (String badgeCode : json.badge.split(";")) {
                if (habbo.getInventory().getBadgesComponent().hasBadge(badgeCode)) {
                    this.status = RCONMessage.STATUS_ERROR;
                    this.message += Emulator.getTexts().getValue("commands.error.cmd_badge.already_owned").replace("%user%", username).replace("%badge%", badgeCode) + "\r";
                    continue;
                }

                HabboBadge badge = new HabboBadge(0, badgeCode, 0, habbo);

                badge.run();

                habbo.getInventory().getBadgesComponent().addBadge(badge);
                habbo.getClient().sendResponse(new AddUserBadgeComposer(badge));

                this.message = Emulator.getTexts().getValue("commands.succes.cmd_badge.given").replace("%user%", username).replace("%badge%", badgeCode);
            }
        } else {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
                for (String badgeCode : json.badge.split(";")) {
                    int numberOfRows = 0;
                    try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(slot_id) FROM users_badges INNER JOIN users ON users.id = user_id WHERE users.id = ? AND badge_code = ? LIMIT 1")) {
                        statement.setInt(1, json.user_id);
                        statement.setString(2, badgeCode);
                        try (ResultSet set = statement.executeQuery()) {
                            if (set.next()){
                                numberOfRows = set.getInt(1);
                            }
                        }
                    }

                    if (numberOfRows != 0) {
                        this.status = RCONMessage.STATUS_ERROR;
                        this.message += Emulator.getTexts().getValue("commands.error.cmd_badge.already_owns").replace("%user%", username).replace("%badge%", badgeCode) + "\r";
                    } else {
                        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users_badges VALUES (null, (SELECT id FROM users WHERE users.id = ? LIMIT 1), 0, ?)", Statement.RETURN_GENERATED_KEYS)) {
                            statement.setInt(1, json.user_id);
                            statement.setString(2, badgeCode);
                            statement.execute();
                        }

                        this.message = Emulator.getTexts().getValue("commands.succes.cmd_badge.given").replace("%user%", username).replace("%badge%", badgeCode);
                    }
                }
            } catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
                this.status = RCONMessage.STATUS_ERROR;
                this.message = e.getMessage();
            }
        }
    }

    static class GiveBadgeJSON {

        public int user_id = -1;


        public String badge;
    }
}
