package ch.realmtech.server.mod.factory;

import ch.realmtech.server.level.ClickInteractionItemClient;
import ch.realmtech.server.packet.serverPacket.EatItemPacket;
import ch.realmtech.server.packet.serverPacket.PlayerWeaponShotPacket;

import java.util.UUID;

public class ItemInteractionCommun {
    public static ClickInteractionItemClient eat() {
        return (clientContext, event, cellId, itemId) -> {
            UUID itemUuid = clientContext.getSystemsAdminClient().getUuidEntityManager().getEntityUuid(itemId);
            clientContext.sendRequest(new EatItemPacket(itemUuid));
        };
    }

    public static ClickInteractionItemClient attack(boolean playerSwordSwing) {
        if (playerSwordSwing) {
            return (clientContext, event, cellId, itemId) -> {
                clientContext.getSoundManager().playSwordSwing();
                UUID itemUuid = clientContext.getSystemsAdminClient().getUuidEntityManager().getEntityUuid(itemId);
                clientContext.sendRequest(new PlayerWeaponShotPacket(event.gameCoordinateX(), event.gameCoordinateY(), itemUuid));
            };
        } else {
            return (clientContext, event, cellId, itemId) -> {
                UUID itemUuid = clientContext.getSystemsAdminClient().getUuidEntityManager().getEntityUuid(itemId);
                clientContext.sendRequest(new PlayerWeaponShotPacket(event.gameCoordinateX(), event.gameCoordinateY(), itemUuid));
            };
        }
    }
}
