package ch.realmtech.server.level.cell;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.RightClickInteraction;
import ch.realmtech.server.mod.PlayerFootStepSound;
import ch.realmtech.server.mod.RealmTechCoreMod;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CellBehavior {
    private ItemType breakWith;
    private float speedEffect = 1;
    private PlayerFootStepSound playerFootStepSound;
    private final byte layer;
    private BreakCell breakCellEvent;
    private Supplier<BreakCell> getBreakCellEvent;
    private int breakStepNeed = 20;
    private CreatePhysiqueBody createBody;
    private BiConsumer<com.badlogic.gdx.physics.box2d.World, Body> deleteBody;
    private EditEntity editEntityOnCreate;
    private RightClickInteraction interagieClickDroit;
    private int tiledTextureX;
    private int tiledTextureY;

    public static CellBehaviorBuilder builder(byte layer) {
        return new CellBehaviorBuilder(layer);
    }

    public static CellBehaviorBuilder builder(Cells.Layer layer) {
        return builder(layer.layer);
    }

    private CellBehavior(byte layer) {
        this.layer = layer;
    }

    public ItemType getBreakWith() {
        return breakWith;
    }

    public float getSpeedEffect() {
        return speedEffect;
    }

    public PlayerFootStepSound getPlayerFootStepSound() {
        return playerFootStepSound;
    }

    public byte getLayer() {
        return layer;
    }

    public BreakCell getBreakCellEvent() {
        if (breakCellEvent == null && getBreakCellEvent != null) breakCellEvent = getBreakCellEvent.get();
        return breakCellEvent != null ? breakCellEvent : BreakCellEvent.dropNothing();
    }

    public int getBreakStepNeed() {
        return breakStepNeed;
    }

    public Optional<EditEntity> getEditEntity() {
        return Optional.ofNullable(editEntityOnCreate);
    }

    public CreatePhysiqueBody getCreateBody() {
        return createBody;
    }

    public BiConsumer<World, Body> getDeleteBody() {
        return deleteBody;
    }

    public Optional<RightClickInteraction> getInteragieClickDroit() {
        return Optional.ofNullable(interagieClickDroit);
    }

    public int getTiledTextureX() {
        return tiledTextureX;
    }

    public int getTiledTextureY() {
        return tiledTextureY;
    }

    public static class CellBehaviorBuilder {
        private final CellBehavior cellBehavior;

        public CellBehaviorBuilder(byte layer) {
            cellBehavior = new CellBehavior(layer);
        }

        public CellBehaviorBuilder(Cells.Layer ground) {
            this(ground.layer);
        }

        public CellBehaviorBuilder breakWith(ItemType itemType) {
            cellBehavior.breakWith = itemType;
            return this;
        }

        public CellBehaviorBuilder breakWith(ItemType itemType, String dropItemRegistryName) {
            breakWith(itemType);
            dropOnBreak(dropItemRegistryName);
            return this;
        }

        public CellBehaviorBuilder dropOnBreak(String dropItemRegistryName) {
            cellBehavior.getBreakCellEvent = () -> BreakCellEvent.dropOnBreak(RealmTechCoreMod.ITEMS.get(dropItemRegistryName).getEntry());
            return this;
        }

        public CellBehaviorBuilder dropNothing() {
            cellBehavior.getBreakCellEvent = BreakCellEvent::dropNothing;
            return this;
        }

        public CellBehaviorBuilder speedEffect(float speedEffect) {
            cellBehavior.speedEffect = speedEffect;
            return this;
        }

        public CellBehaviorBuilder playerWalkSound(String soundEffectName, float volume) {
            cellBehavior.playerFootStepSound = new PlayerFootStepSound(soundEffectName, volume);
            return this;
        }

        public CellBehaviorBuilder editEntity(EditEntity... editEntity) {
            cellBehavior.editEntityOnCreate = new EditEntity() {
                @Override
                public void createEntity(ExecuteOnContext executeOnContext, int entityId) {
                    for (EditEntity entity : editEntity) {
                        entity.createEntity(executeOnContext, entityId);
                    }
                }

                @Override
                public void deleteEntity(ExecuteOnContext executeOnContext, int entityId) {
                    for (EditEntity entity : editEntity) {
                        entity.deleteEntity(executeOnContext, entityId);
                    }
                }

                @Override
                public void replaceEntity(ExecuteOnContext executeOnContext, int entityId) {
                    for (EditEntity entity : editEntity) {
                        entity.replaceEntity(executeOnContext, entityId);
                    }
                }
            };
            return this;
        }

        /**
         * Par défaut, 20
         */
        public CellBehaviorBuilder breakStepNeed(int steepNeed) {
            cellBehavior.breakStepNeed = steepNeed;
            return this;
        }

        public CellBehaviorBuilder physiqueBody(CreatePhysiqueBody.CreatePhysiqueBodyArgs createPhysiqueBody) {
            cellBehavior.createBody = createPhysiqueBody.createPhysiqueBody();
            cellBehavior.deleteBody = createPhysiqueBody.deletePhysiqueBody();
            return this;
        }

        public CellBehaviorBuilder interagieClickDroit(RightClickInteraction interagieClickDroit) {
            cellBehavior.interagieClickDroit = interagieClickDroit;
            return this;
        }

        public CellBehaviorBuilder tiledTexture(int maxX, int maxY) {
            cellBehavior.tiledTextureX = maxX;
            cellBehavior.tiledTextureY = maxY;
            return this;
        }

        public CellBehavior build() {
            return cellBehavior;
        }
    }
}
