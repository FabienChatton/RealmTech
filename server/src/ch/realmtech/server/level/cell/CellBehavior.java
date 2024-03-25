package ch.realmtech.server.level.cell;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.ClickInteraction;
import ch.realmtech.server.mod.PlayerFootStepSound;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Optional;
import java.util.function.BiConsumer;

public class CellBehavior {
    private ItemType breakWith;
    private float speedEffect = 1;
    private PlayerFootStepSound playerFootStepSound;
    private final byte layer;
    private BreakCell breakCellEvent;
    private String dropItemRegistryName;
    private int breakStepNeed = 20;
    private CreatePhysiqueBody createBody;
    private BiConsumer<com.badlogic.gdx.physics.box2d.World, Body> deleteBody;
    private EditEntity editEntity;
    private ClickInteraction interagieClickDroit;
    private int tiledTextureX;
    private int tiledTextureY;
    private boolean canPlaceCellOnTop = true;

    public static CellBehaviorBuilder builder(byte layer) {
        return new CellBehaviorBuilder(layer);
    }

    public static CellBehaviorBuilder builder(Cells.Layer layer) {
        return builder(layer.layer);
    }

    protected CellBehavior(byte layer) {
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
        return breakCellEvent != null ? breakCellEvent : BreakCellEvent.dropNothing();
    }

    public int getBreakStepNeed() {
        return breakStepNeed;
    }

    public Optional<EditEntity> getEditEntity() {
        return Optional.ofNullable(editEntity);
    }

    public CreatePhysiqueBody getCreateBody() {
        return createBody;
    }

    public BiConsumer<World, Body> getDeleteBody() {
        return deleteBody;
    }

    public Optional<ClickInteraction> getInteragieClickDroit() {
        return Optional.ofNullable(interagieClickDroit);
    }

    public int getTiledTextureX() {
        return tiledTextureX;
    }

    public int getTiledTextureY() {
        return tiledTextureY;
    }

    public boolean isCanPlaceCellOnTop() {
        return canPlaceCellOnTop;
    }

    public String getDropItemRegistryName() {
        return dropItemRegistryName;
    }

    public void setBreakCellEvent(BreakCell breakCell) {
        this.breakCellEvent = breakCell;
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
            cellBehavior.dropItemRegistryName = dropItemRegistryName;
            return this;
        }

        public CellBehaviorBuilder dropNothing() {
            cellBehavior.dropItemRegistryName = null;
            return this;
        }

        public CellBehaviorBuilder speedEffect(float speedEffect) {
            cellBehavior.speedEffect = speedEffect;
            return this;
        }

        public CellBehaviorBuilder playerWalkSound(float volume, String... soundEffectName) {
            cellBehavior.playerFootStepSound = new PlayerFootStepSound(volume, soundEffectName);
            return this;
        }

        public CellBehaviorBuilder editEntity(EditEntity... editEntity) {
            cellBehavior.editEntity = new EditEntity() {
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

        public CellBehaviorBuilder interagieClickDroit(ClickInteraction interagieClickDroit) {
            cellBehavior.interagieClickDroit = interagieClickDroit;
            return this;
        }

        public CellBehaviorBuilder tiledTexture(int maxX, int maxY) {
            cellBehavior.tiledTextureX = maxX;
            cellBehavior.tiledTextureY = maxY;
            return this;
        }

        /**
         * default true
         */
        public CellBehaviorBuilder canPlaceCellOnTop(boolean canPlaceCellOnTop) {
            cellBehavior.canPlaceCellOnTop = canPlaceCellOnTop;
            return this;
        }

        public CellBehavior build() {
            return cellBehavior;
        }
    }
}
