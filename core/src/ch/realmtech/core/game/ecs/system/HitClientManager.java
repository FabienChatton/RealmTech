package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.component.TextureColorComponent;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.plugin.forclient.HitManagerForClient;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;

import java.util.UUID;

public class HitClientManager extends Manager implements HitManagerForClient {

    @Wire(name = "context")
    private RealmTech context;
    @Wire
    private SystemsAdminClient systemsAdminClient;
    private ComponentMapper<Box2dComponent> mBox2d;

    public void entityJustHit(UUID entityUuid) {
        int entityId = systemsAdminClient.getUuidEntityManager().getEntityId(entityUuid);
        world.edit(entityId).create(TextureColorComponent.class).set(Color.RED);
        systemsAdminClient.getParticleEffectsSystem().createHitEffect(mBox2d.get(entityId).body.getPosition());
        context.nextTickSimulation(60, () -> world.edit(entityId).remove(TextureColorComponent.class));
    }
}
