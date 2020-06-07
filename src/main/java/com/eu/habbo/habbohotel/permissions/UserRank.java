package com.eu.habbo.habbohotel.permissions;

import gnu.trove.map.hash.THashMap;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class UserRank {

    private final int user_id;

    private final THashMap<String, UserPermission> permissions;
    private final THashMap<String, String> variables;

    public UserRank(ResultSet set) throws SQLException {
        this.permissions = new THashMap<>();
        this.variables = new THashMap<>();
        this.user_id = set.getInt("user_id");

        this.load(set);
    }

    public void load(ResultSet set) throws SQLException {
        ResultSetMetaData meta = set.getMetaData();
        for (int i = 1; i < meta.getColumnCount() + 1; i++) {
            String columnName = meta.getColumnName(i);
            if (columnName.startsWith("cmd_") || columnName.startsWith("acc_")) {
                this.permissions.put(meta.getColumnName(i), new UserPermission(columnName, UserPermissionSetting.fromString(set.getString(i))));
            } else {
                this.variables.put(meta.getColumnName(i), set.getString(i));
            }
        }
    }

    public boolean hasPermission(String key, boolean isRoomOwner) {
        if (this.permissions.containsKey(key)) {
            UserPermission permission = this.permissions.get(key);

            return permission.setting == UserPermissionSetting.ALLOWED || permission.setting == UserPermissionSetting.ROOM_OWNER && isRoomOwner;

        }

        return false;
    }

    public boolean permissionIgnored(String key) {
        if (this.permissions.containsKey(key)) {
            UserPermission permission = this.permissions.get(key);
            return permission.setting == UserPermissionSetting.IGNORED;
        }

        return true;
    }

    public THashMap<String, UserPermission> getPermissions() {
        return this.permissions;
    }

    public THashMap<String, String> getVariables() {
        return this.variables;
    }
}

