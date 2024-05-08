package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.level.ClickInteractionItemClient;
import ch.realmtech.server.packet.serverPacket.PlayerWeaponShotPacket;
import ch.realmtech.server.registry.ItemEntry;

import java.util.UUID;

public class WeaponItemEntry extends ItemEntry {
    public WeaponItemEntry() {
        super("Weapon", "p2022", ItemBehavior.builder()
                .setAttackDommage(5)
                .setAttackRange(10)
                .setFireArm()
                .build());
    }

    @Override
    public ClickInteractionItemClient getLeftClickOnJustPressed() {
        return (clientContext, event, cellId, itemId) -> {
            UUID itemUuid = clientContext.getSystemsAdminClient().getUuidEntityManager().getEntityUuid(itemId);
            clientContext.sendRequest(new PlayerWeaponShotPacket(event.gameCoordinateX(), event.gameCoordinateY(), itemUuid));
        };
    }
}
