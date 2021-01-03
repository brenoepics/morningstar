package com.eu.habbo.habbohotel.messenger;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.friends.FriendChatMessageComposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OfflineMessengerOffline {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfflineMessengerOffline.class);

    private static HashMap<Integer, List<Message>> offlineMessages = new HashMap<>();

    public static void init() {
        offlineMessages.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM messenger_offline")) {
                try (ResultSet set = statement.executeQuery()) {
                    while (set.next()) {
                        Message message = new Message(set.getInt("user_from_id"), set.getInt("user_to_id"), set.getString("message"));
                        if (message == null)
                            continue;

                        message.setTimestamp(set.getInt("timestamp"));

                        List<Message> messages = (!offlineMessages.containsKey(message.getToId()) ? (new ArrayList<>()) : (offlineMessages.get(message.getToId())));
                        messages.add(message);

                        offlineMessages.put(message.getToId(), messages);
                    }
                } catch (SQLException sql) {
                    LOGGER.error(sql.toString());
                }
            }
        } catch (SQLException sql) {
            LOGGER.error(sql.toString());
        }
    }

    public static void addOfflineMessage(Message message) {
        if (message == null)
            return;

        message.setTimestamp(Emulator.getIntUnixTimestamp());

        List<Message> messages = (!offlineMessages.containsKey(message.getToId()) ? (new ArrayList<>()) : (offlineMessages.get(message.getToId())));
        messages.add(message);
        offlineMessages.put(message.getToId(), messages);
    }

    public static void readOfflineMessages(Habbo habbo) {
        if (habbo != null && habbo.getClient() != null) {
            int userId = habbo.getHabboInfo().getId();

            if (offlineMessages.containsKey(userId)) {
                for (Message message : offlineMessages.get(userId))
                    habbo.getClient().sendResponse(new FriendChatMessageComposer(message));

                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
                    try (PreparedStatement statement = connection.prepareStatement("DELETE FROM messenger_offline WHERE user_to_id = ?")) {
                        statement.setInt(1, userId);
                        statement.execute();
                    }
                } catch (SQLException e) {
                    LOGGER.error("Caught SQL exception", e);
                }

                offlineMessages.remove(userId);
            }
        }
    }

    public static void saveToDatabase() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            for (List<Message> messages : offlineMessages.values()) {
                for (Message message : messages) {
                    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO messenger_offline (user_to_id, user_from_id, message, timestamp) VALUES (?, ?, ?, ?);")) {
                        statement.setInt(1, message.getToId());
                        statement.setInt(2, message.getFromId());
                        statement.setString(3, message.getMessage());
                        statement.setInt(4, message.getTimestamp());
                        statement.execute();
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }
}
