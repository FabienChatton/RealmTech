package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.level.ClickInteractionItemClient;
import ch.realmtech.server.packet.serverPacket.EatItemPacket;
import ch.realmtech.server.registry.ItemEntry;

import java.util.UUID;

public class RawChickenItemEntry extends ItemEntry {
    public RawChickenItemEntry() {
        super("RawChicken", "plank-02", ItemBehavior.builder()
                .eatRestore(2)
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
