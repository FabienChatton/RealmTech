package ch.realmtech.server.enemy;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.registry.*;
import com.artemis.EntityEdit;
import com.artemis.World;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.UUID;
import java.util.function.IntConsumer;

public class MobBehavior implements Evaluator {
    private EditEntity editEntity;
    private int attackDommage = 1;
    private int attackCoolDownTick = 15;
    private int heart = 10;
    private String dropItemFqrn;
    private ItemEntry dropItem;
    private EnemyTexture enemyTexture;
    private final MobEntry mobEntry;
    private boolean focusPlayer = false;

    public MobBehavior(MobEntry mobEntry) {
        this.mobEntry = mobEntry;
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        editEntity = EditEntity.merge(new EditEntity() {
            @Override
            public void createEntity(ExecuteOnContext executeOnContext, int entityId) {
                executeOnContext.onCommun((world) -> {
                    EntityEdit edit = world.edit(entityId);
                    edit.create(MobComponent.class).set(mobEntry);
                    edit.create(PositionComponent.class);
                });

                executeOnContext.onServer((serverContext) -> {
                    World world = serverContext.getEcsEngineServer().getWorld();

                    EntityEdit edit = world.edit(entityId);
                    Body enemyBody = serverContext.getSystemsAdminServer().getEnemyManagerCommun().createEnemyBody(edit);
                    IntConsumer updateEnemy;
                    if (focusPlayer) {
                        updateEnemy = serverContext.getSystemsAdminServer().getIaMobFocusPlayerSystem().enemyFocusPlayer();
                    } else {
                        updateEnemy = value -> {
                        };
                    }
                    edit.create(EnemyComponent.class).set(new EnemyTelegraph(entityId, serverContext), new EnemySteerable(enemyBody, 4), updateEnemy);
                    edit.create(EnemyHitPlayerComponent.class);

                    serverContext.getSystemsAdminServer().getUuidEntityManager().registerEntityIdWithUuid(UUID.randomUUID(), entityId);
                });

                executeOnContext.onClientWorld((clientForClient, world) -> {
                    EntityEdit edit = world.edit(entityId);

                    clientForClient.getEnemyManagerCommun().createEnemyBody(edit);

                    edit.create(MouvementComponent.class);
                });
            }

            @Override
            public void deleteEntity(ExecuteOnContext executeOnContext, int entityId) {
                executeOnContext.onCommun((world) -> {
                    SystemsAdminCommun systemsAdmin = world.getRegistered("systemsAdmin");
                    systemsAdmin.getEnemyManagerCommun().destroyWorldEnemy(entityId);
                });
            }
        }, editEntity);



        editEntity = EditEntity.merge(EditEntity.create((executeOnContext, entityId) -> {
            executeOnContext.onServer((serverContext) -> serverContext.getEcsEngineServer().getWorld().getMapper(LifeComponent.class).create(entityId)
                    .set(heart));
        }), editEntity);

        if (dropItemFqrn != null) {
            dropItem = RegistryUtils.evaluateSafe(rootRegistry, dropItemFqrn, ItemEntry.class);
            editEntity = EditEntity.merge(EditEntity.delete((executeOnContext, entityId) -> executeOnContext.onServer((serverContext) -> {
                serverContext.getSystemsAdminServer().getMobSystem().dropItemOnDead(entityId, dropItem);
            })), editEntity);
        }

        if (enemyTexture != null) {
            editEntity = EditEntity.merge(enemyTexture.createTexture(), editEntity);
        }
    }

    public static MobBehaviorBuilder builder(MobEntry mobEntry) {
        return builder(mobEntry, EditEntity.empty());
    }

    public static MobBehaviorBuilder builder(MobEntry mobEntry, EditEntity editEntity) {
        return new MobBehaviorBuilder(mobEntry, editEntity);
    }

    public EditEntity getEditEntity() {
        return editEntity;
    }

    public int getAttackDommage() {
        return attackDommage;
    }

    public int getAttackCoolDownTick() {
        return attackCoolDownTick;
    }

    public boolean isFocusPlayer() {
        return focusPlayer;
    }

    public static class MobBehaviorBuilder {
        private final MobBehavior mobBehavior;

        private MobBehaviorBuilder(MobEntry mobEntry, EditEntity editEntity) {
            mobBehavior = new MobBehavior(mobEntry);
            mobBehavior.editEntity = editEntity;
        }

        public MobBehaviorBuilder editEntity(EditEntity editEntity) {
            mobBehavior.editEntity = editEntity;
            return this;
        }

        /**
         * default 1 heart dommage
         */
        public MobBehaviorBuilder attackDommage(int attackDommage) {
            mobBehavior.attackDommage = attackDommage;
            return this;
        }

        /**
         * default 15 tick attack cool down
         */
        public MobBehaviorBuilder attackCoolDown(int attackCoolDownTick) {
            mobBehavior.attackCoolDownTick = attackCoolDownTick;
            return this;
        }

        public MobBehaviorBuilder dropItemFqrn(String dropItemFqrn) {
            mobBehavior.dropItemFqrn = dropItemFqrn;
            return this;
        }

        /**
         * default 10 heart
         */
        public MobBehaviorBuilder heart(int heart) {
            mobBehavior.heart = heart;
            return this;
        }

        public MobBehaviorBuilder textures(EnemyTexture enemyTexture) {
            mobBehavior.enemyTexture = enemyTexture;
            return this;
        }

        /**
         * default false
         */
        public MobBehaviorBuilder focusPlayer() {
            mobBehavior.focusPlayer = true;
            return this;
        }

        public MobBehavior build() {
            return mobBehavior;
        }
    }
}
