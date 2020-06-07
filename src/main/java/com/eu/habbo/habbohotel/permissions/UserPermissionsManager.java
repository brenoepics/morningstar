package com.eu.habbo.habbohotel.permissions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserPermissionsManager {

    private final TIntObjectHashMap<UserRank> userRanks;

    public UserPermissionsManager() {
        long millis = System.currentTimeMillis();
        this.userRanks = new TIntObjectHashMap<>();
    }

    public void reload() {
        this.loadUserPermissions();
    }

    private void loadUserPermissions() {
        String onlineHabbos = "";
        for(Habbo habbo: Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().values()) {
            onlineHabbos += habbo.getHabboInfo().getId() + ", ";
        }
        onlineHabbos = onlineHabbos.substring(0, onlineHabbos.length() - 2);
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM user_permissions WHERE user_id IN (?)")) {
            statement.setString(1, onlineHabbos);
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    UserRank userRank;
                    if (!this.userRanks.containsKey(set.getInt("user_id"))) {
                        userRank = new UserRank(set);
                        this.userRanks.put(set.getInt("user_id"), userRank);
                    } else {
                        userRank = this.userRanks.get(set.getInt("user_id"));
                        userRank.load(set);
                    }
                }
            }
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public void loadPermissionsForHabbo(Habbo habbo) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM user_permissions WHERE user_id = ? LIMIT 1")) {
            statement.setInt(1, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    UserRank userRank;
                    if (!this.userRanks.containsKey(set.getInt("user_id"))) {
                        userRank = new UserRank(set);
                        this.userRanks.put(set.getInt("user_id"), userRank);
                    } else {
                        userRank = this.userRanks.get(set.getInt("user_id"));
                        userRank.load(set);
                    }
                }
            }
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public void unloadPermissionsForHabbo(Habbo habbo) {
        if (this.userRanks.containsKey(habbo.getHabboInfo().getId())) {
            this.userRanks.remove(habbo.getHabboInfo().getId());
        }
    }

    public UserRank getUserRank(int userId) {
        return this.userRanks.get(userId);
    }

    public boolean hasPermission(Habbo habbo, String permission, boolean withRoomRights) {
        if (this.userRanks.containsKey(habbo.getHabboInfo().getId())) {
            return this.getUserRank(habbo.getClient().getHabbo().getHabboInfo().getId()).hasPermission(permission, withRoomRights);
        }
        return false;
    }

    public boolean permissionIgnored(Habbo habbo, String permission) {
        if (this.userRanks.containsKey(habbo.getHabboInfo().getId())) {
            return this.getUserRank(habbo.getClient().getHabbo().getHabboInfo().getId()).permissionIgnored(permission);
        }
        return true;
    }

    public boolean userExists(int userId) {
        return this.userRanks.containsKey(userId);
    }
}
