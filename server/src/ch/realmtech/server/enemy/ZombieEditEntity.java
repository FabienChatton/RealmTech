package ch.realmtech.server.enemy;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.component.EnemyHitPlayerComponent;
import ch.realmtech.server.ecs.component.MobComponent;
import ch.realmtech.server.ecs.component.MouvementComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.registry.MobEntry;
import com.artemis.EntityEdit;
import com.artemis.World;
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

            clientForClient.getEnemyManagerCommun().createEnemyBody(edit);

            edit.create(MouvementComponent.class);
        });

        executeOnContext.onServer((serverContext) -> {
            World world = serverContext.getEcsEngineServer().getWorld();

            EntityEdit edit = world.edit(entityId);
            Body enemyBody = serverContext.getSystemsAdminServer().getEnemyManagerCommun().createEnemyBody(edit);
            edit.create(EnemyComponent.class).set(new EnemyTelegraph(entityId, serverContext), new EnemySteerable(enemyBody, 4), serverContext.getSystemsAdminServer().getIaMobFocusPlayerSystem().enemyFocusPlayer());
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
