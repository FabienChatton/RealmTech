package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.level.ClickInteractionItemClient;
import ch.realmtech.server.packet.serverPacket.EatItemPacket;
import ch.realmtech.server.registry.ItemEntry;

import java.util.UUID;

public class ChickenNuggets extends ItemEntry {
    public ChickenNuggets() {
        super("ChickenNuggets", "copper-ingot-01", ItemBehavior.builder()
                .eatRestore(4)
                .build());
    }

    @Override
    public ClickInteractionItemClient getRightClickOnJustPressed() {
        return (clientContext, event, cellId, itemId) -> {
            UUID itemUuid = clientContext.getSystemsAdminClient().getUuidEntityManager().getEntityUuid(itemId);
            clientContext.sendRequest(new EatItemPacket(itemUuid));
        };
    }
}
