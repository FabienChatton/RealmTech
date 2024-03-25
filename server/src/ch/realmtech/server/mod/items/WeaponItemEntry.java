package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.level.ClickInteractionItemClient;
import ch.realmtech.server.packet.serverPacket.PlayerWeaponShotPacket;
import ch.realmtech.server.registry.ItemEntry;

public class WeaponItemEntry extends ItemEntry {
    public WeaponItemEntry() {
        super("Weapon", "pioche-stone-01", ItemBehavior.builder().build());
    }

    @Override
    public ClickInteractionItemClient getLeftClickInteraction() {
        return (clientContext, event, itemId, cellTargetId) -> {
            clientContext.sendRequest(new PlayerWeaponShotPacket(event.gameCoordinateX(), event.gameCoordinateY()));
        };
    }
}
