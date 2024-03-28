package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.level.ClickInteractionItemClient;
import ch.realmtech.server.packet.serverPacket.PlayerWeaponShotPacket;
import ch.realmtech.server.registry.ItemEntry;
import com.badlogic.gdx.math.Vector2;

public class WeaponItemEntry extends ItemEntry {
    public WeaponItemEntry() {
        super("Weapon", "pioche-stone-01", ItemBehavior.builder().build());
    }

    @Override
    public ClickInteractionItemClient getLeftClickOnJustPressed() {
        return (clientContext, event, itemId, cellTargetId) -> {
            clientContext.sendRequest(new PlayerWeaponShotPacket(event.gameCoordinateX(), event.gameCoordinateY()));
            clientContext.getSystemsAdminClient().getParticleEffectsSystem().createHitEffect(new Vector2(event.gameCoordinateX(), event.gameCoordinateY()));
        };
    }
}
