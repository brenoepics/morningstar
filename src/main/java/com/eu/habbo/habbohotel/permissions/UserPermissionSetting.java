package com.eu.habbo.habbohotel.permissions;

public enum UserPermissionSetting {

    IGNORED,


    DISALLOWED,


    ALLOWED,


    ROOM_OWNER;

    public static UserPermissionSetting fromString(String value) {
        switch (value) {
            case "-1":
                return IGNORED;
            case "1":
                return ALLOWED;
            case "2":
                return ROOM_OWNER;

        }

        return DISALLOWED;
    }
}
