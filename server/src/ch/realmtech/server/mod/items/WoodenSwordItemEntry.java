package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.level.ClickInteractionItemClient;
import ch.realmtech.server.packet.serverPacket.PlayerWeaponShotPacket;
import ch.realmtech.server.registry.ItemEntry;

import java.util.UUID;

public class WoodenSwordItemEntry extends ItemEntry {
    public WoodenSwordItemEntry() {
        super("WoodenSword", "wooden-sword-01", ItemBehavior.builder()
                .setAttackRange(5)
                .setAttackRange(4)
                .build());
    }

    @Override
    public ClickInteractionItemClient getLeftClickOnJustPressed() {
        return (clientContext, event, cellId, itemId) -> {
            clientContext.getSoundManager().playSwordSwing();
            UUID itemUuid = clientContext.getSystemsAdminClient().getUuidEntityManager().getEntityUuid(itemId);
            clientContext.sendRequest(new PlayerWeaponShotPacket(event.gameCoordinateX(), event.gameCoordinateY(), itemUuid));
        };
    }
}
