package ch.realmtech.server.enemy;

import ch.realmtech.server.ecs.component.LifeComponent;
import ch.realmtech.server.ecs.component.TextureAnimationComponent;
import ch.realmtech.server.ecs.component.TextureComponent;
import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.registry.*;
import com.artemis.EntityEdit;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MobBehavior implements Evaluator {
    private EditEntity editEntity;
    private int attackDommage = 1;
    private int attackCoolDownTick = 15;
    private int heart = 10;
    private String dropItemFqrn;
    private ItemEntry dropItem;
    private String[] textureRegionNames;

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
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

        if (textureRegionNames != null) {
            editEntity = EditEntity.merge(EditEntity.create((executeOnContext, entityId) -> {
                executeOnContext.onClientWorld((clientForClient, world) -> {
                    EntityEdit edit = world.edit(entityId);
                    TextureAtlas textureAtlas = world.getRegistered(TextureAtlas.class);

                    TextureComponent textureComponent = edit.create(TextureComponent.class);
                    textureComponent.scale = 1.6f;

                    TextureAnimationComponent textureAnimationComponent = edit.create(TextureAnimationComponent.class);
                    TextureRegion[] textureRegions = new TextureRegion[textureRegionNames.length];
                    for (int i = 0; i < textureRegionNames.length; i++) {
                        textureRegions[i] = textureAtlas.findRegion(textureRegionNames[i]);
                    }
                    textureAnimationComponent.animationFront = textureRegions;
                });
            }), editEntity);
        }
    }

    public static MobBehaviorBuilder builder(EditEntity editEntity) {
        return new MobBehaviorBuilder(editEntity);
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

    public static class MobBehaviorBuilder {
        private final MobBehavior mobBehavior;

        private MobBehaviorBuilder(EditEntity editEntity) {
            mobBehavior = new MobBehavior();
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

        public MobBehaviorBuilder textures(String... textureRegionNames) {
            mobBehavior.textureRegionNames = textureRegionNames;
            return this;
        }

        public MobBehavior build() {
            return mobBehavior;
        }
    }
}
