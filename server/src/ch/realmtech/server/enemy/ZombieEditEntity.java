package ch.realmtech.server.enemy;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.registry.MobEntry;
import com.artemis.EntityEdit;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.UUID;

public class ZombieEditEntity implements EditEntity {
    private final MobEntry mobEntry;

    public ZombieEditEntity(MobEntry mobEntry) {
        this.mobEntry = mobEntry;
    }

    @Override
    public void createEntity(ExecuteOnContext executeOnContext, int entityId) {
        executeOnContext.onClientWorld((clientForClient, world) -> {
            EntityEdit edit = world.edit(entityId);
            TextureAtlas textureAtlas = world.getRegistered(TextureAtlas.class);

            TextureComponent textureComponent = edit.create(TextureComponent.class);
            textureComponent.scale = 1.6f;

            TextureAnimationComponent textureAnimationComponent = edit.create(TextureAnimationComponent.class);
            TextureAtlas.AtlasRegion textureFront0 = textureAtlas.findRegion("zombie-0");
            TextureAtlas.AtlasRegion textureFront1 = textureAtlas.findRegion("zombie-1");
            TextureAtlas.AtlasRegion textureFront2 = textureAtlas.findRegion("zombie-2");
            textureAnimationComponent.animationFront = new TextureRegion[]{textureFront0, textureFront1, textureFront2};

            clientForClient.getEnemyManagerCommun().createEnemyBody(edit);

            edit.create(MouvementComponent.class);
        });

        executeOnContext.onServer((serverContext) -> {
            World world = serverContext.getEcsEngineServer().getWorld();

            EntityEdit edit = world.edit(entityId);
            Body enemyBody = serverContext.getSystemsAdminServer().getEnemyManagerCommun().createEnemyBody(edit);
            edit.create(EnemyComponent.class).set(new EnemyTelegraph(entityId, serverContext), new EnemySteerable(enemyBody, 4), serverContext.getSystemsAdminServer().getIaMobFocusPlayerSystem().enemyFocusPlayer());
            edit.create(LifeComponent.class).set(10);
            edit.create(EnemyHitPlayerComponent.class);

            serverContext.getSystemsAdminServer().getUuidEntityManager().registerEntityIdWithUuid(UUID.randomUUID(), entityId);
        });

        executeOnContext.onCommun((world) -> {
            EntityEdit edit = world.edit(entityId);
            edit.create(MobComponent.class).set(mobEntry);
            edit.create(PositionComponent.class);
        });
    }

    @Override
    public void deleteEntity(ExecuteOnContext executeOnContext, int entityId) {
        executeOnContext.onCommun((world) -> {
            SystemsAdminCommun systemsAdmin = world.getRegistered("systemsAdmin");
            systemsAdmin.getEnemyManagerCommun().destroyWorldEnemy(entityId);
        });
    }
}
