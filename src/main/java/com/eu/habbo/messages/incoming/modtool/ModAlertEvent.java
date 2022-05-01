package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolSanctionItem;
import com.eu.habbo.habbohotel.modtool.ModToolSanctions;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.IssueCloseNotificationMessageComposer;
import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;

public class ModAlertEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int userId = this.packet.readInt();
        String message = this.packet.readString();
        int cfhTopic = this.packet.readInt();

        if (!this.client.getHabbo().hasPermission(Permission.ACC_SUPPORTTOOL)) return;
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);

        if (habbo == null) {
            this.client.sendResponse(new IssueCloseNotificationMessageComposer(Emulator.getTexts().getValue("generic.user.not_found").replace("%user%", Emulator.getConfig().getValue("hotel.player.name"))));
            return;
        }
        ModToolSanctions modToolSanctions = Emulator.getGameEnvironment().getModToolSanctions();

        if (!Emulator.getConfig().getBoolean("hotel.sanctions.enabled")) {
            habbo.alert(message);
            return;
        }
        THashMap<Integer, ArrayList<ModToolSanctionItem>> modToolSanctionItemsHashMap = Emulator.getGameEnvironment().getModToolSanctions().getSanctions(habbo.getHabboInfo().getId());
        ArrayList<ModToolSanctionItem> modToolSanctionItems = modToolSanctionItemsHashMap.get(habbo.getHabboInfo().getId());

        if (modToolSanctionItems == null || modToolSanctionItems.isEmpty()) {
            modToolSanctions.run(userId, this.client.getHabbo(), 0, cfhTopic, message, 0, false, 0);
            return;
        }

        ModToolSanctionItem item = modToolSanctionItems.get(modToolSanctionItems.size() - 1);
        if (item == null) return;
        modToolSanctions.run(userId, this.client.getHabbo(), item.sanctionLevel, cfhTopic, message, 0, false, 0);


    }
}