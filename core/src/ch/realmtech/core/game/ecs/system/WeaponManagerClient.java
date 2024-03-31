package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.forclient.WeaponManagerClientClient;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;

import java.util.UUID;

public class WeaponManagerClient extends Manager implements WeaponManagerClientClient {
    @Wire(name = "context")
    private RealmTech context;
    @Wire
    private SystemsAdminClient systemsAdminClient;
    private ComponentMapper<PositionComponent> mPos;

    @Override
    public void playerHasJustShoot(UUID playerUuid) {
        context.getSoundManager().playWeaponShoot();
        int playerId = systemsAdminClient.getPlayerManagerClient().getPlayers().get(playerUuid);
        PositionComponent playerPos = mPos.get(playerId);

        context.getSoundManager().playWeaponShoot();
        int weaponShotLight = world.create();
        systemsAdminClient.getLightManager().createLight(weaponShotLight, Color.WHITE, 10, playerPos.x, playerPos.y);
        context.nextFrame(() -> systemsAdminClient.getLightManager().disposeLight(weaponShotLight));
    }
}
