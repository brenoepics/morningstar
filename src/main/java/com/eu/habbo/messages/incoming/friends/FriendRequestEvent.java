package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.messenger.FriendRequest;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.FriendRequestComposer;
import com.eu.habbo.messages.outgoing.friends.FriendRequestErrorComposer;
import com.eu.habbo.plugin.events.users.friends.UserRequestFriendshipEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class FriendRequestEvent extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FriendRequestEvent.class);

    @Override
    public void handle() throws Exception {
        String username = this.packet.readString();

        if (username == null || username.isEmpty())
            return;

        // If the Habbo you would like to be friends with already requested you to be friends, accept the request
//        FriendRequest friendRequest = this.client.getHabbo().getMessenger().findFriendRequest(username);
//        if (friendRequest != null) {
//            this.client.getHabbo().getMessenger().acceptFriendRequest(friendRequest.getId(), this.client.getHabbo().getHabboInfo().getId());
//            return;
//        }

        // Habbo can be null if the Habbo is not online or when the Habbo doesn't exist
        Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(username);

        int id = 0;
        boolean allowFriendRequests = true;

        // If the Habbo is null, we check the database and set the ID and allowFriendRequests of the Habbo above.
        // If the ID is still 0, the Habbo doesn't exist.
        if (habbo == null) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT users_settings.block_friendrequests, users.id FROM users INNER JOIN users_settings ON users.id = users_settings.user_id WHERE username = ? LIMIT 1")) {
                statement.setString(1, username);
                try (ResultSet set = statement.executeQuery()) {
                    while (set.next()) {
                        id = set.getInt("id");
                        allowFriendRequests = set.getString("block_friendrequests").equalsIgnoreCase("0");
                    }
                }
            } catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
                return;
            }
        } else {
            id = habbo.getHabboInfo().getId();
            allowFriendRequests = !habbo.getHabboStats().blockFriendRequests;

            // Making friends with yourself would be very pathetic, we try to avoid that
            if (id == this.client.getHabbo().getHabboInfo().getId())
                return;

            if (allowFriendRequests)
                habbo.getClient().sendResponse(new FriendRequestComposer(this.client.getHabbo()));
        }

        // The Habbo exists
        if (id != 0) {
            // Check if Habbo is accepting friend requests
            if (!allowFriendRequests) {
                this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.TARGET_NOT_ACCEPTING_REQUESTS));
                return;
            }

            // You can only have x friends
            if (this.client.getHabbo().getMessenger().getFriends().values().size() >= Messenger.friendLimit(this.client.getHabbo()) && !this.client.getHabbo().hasPermission("acc_infinite_friends")) {
                this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.FRIEND_LIST_OWN_FULL));
                return;
            }

            Messenger.makeFriendRequest(this.client.getHabbo().getHabboInfo().getId(), id);

            if (Emulator.getPluginManager().fireEvent(new UserRequestFriendshipEvent(this.client.getHabbo(), username, habbo)).isCancelled()) {
                this.client.sendResponse(new FriendRequestErrorComposer(2));
            }
        } else {
            this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.TARGET_NOT_FOUND));
        }
    }
}
