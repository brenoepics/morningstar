package com.eu.habbo.core;

import com.eu.habbo.Emulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;

public class TextsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextsManager.class);

    private final Properties texts;

    public TextsManager() {
        long millis = System.currentTimeMillis();

        this.texts = new Properties();

        try {
            this.reload();

            LOGGER.info("Texts Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reload() throws Exception {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM emulator_texts")) {
            while (set.next()) {
                if (this.texts.containsKey(set.getString("key"))) {
                    this.texts.setProperty(set.getString("key"), set.getString("value"));
                } else {
                    this.texts.put(set.getString("key"), set.getString("value"));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public boolean hasValue(String key) {
        try {
            return this.texts.containsKey(key);
        } catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }

        return false;
    }

    public String getValue(String key) {
        return this.getValue(key, "");
    }

    public String getValue(String key, String defaultValue) {
        try {
            if (!this.texts.containsKey(key)) {
                LOGGER.error("Text key not found: {}", key);
            }
            return this.texts.getProperty(key, defaultValue);
        } catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }

        return defaultValue;
    }

    public boolean getBoolean(String key) {
        return this.getBoolean(key, false);
    }

    public boolean getBoolean(String key, Boolean defaultValue) {
        try {
            return (this.getValue(key, "0").equals("1")) || (this.getValue(key, "false").equals("true"));
        } catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
        return defaultValue;
    }

    public int getInt(String key) {
        return this.getInt(key, 0);
    }

    public int getInt(String key, Integer defaultValue) {
        try {
            return Integer.parseInt(this.getValue(key, defaultValue.toString()));
        } catch (NumberFormatException e) {
            LOGGER.error("Caught exception", e);
        }
        return defaultValue;
    }

    public void update(String key, String value) {
        try {
            this.texts.setProperty(key, value);
        } catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
    }

    public void updateInDatabase(String key, String value) {
        if (!hasValue(key)) {
            register(key, value);
            return;
        }

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE emulator_texts SET `value`=? WHERE `key`=?;")) {
            statement.setString(1, value);
            statement.setString(2, key);
            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public void register(String key, String value) {
        try {
            if (this.texts.getProperty(key, null) != null)
                return;

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO emulator_texts VALUES (?, ?)")) {
                statement.setString(1, key);
                statement.setString(2, value);
                statement.execute();
            } catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }

            this.update(key, value);
        } catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
    }

    public void remove(String key) {
        try {
            texts.remove(key);
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM emulator_texts WHERE `key`=?;")) {
                statement.setString(1, key);
                statement.execute();
            } catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        } catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
    }
}
