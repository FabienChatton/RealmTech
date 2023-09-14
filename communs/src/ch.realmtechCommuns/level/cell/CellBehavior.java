package ch.realmtechCommuns.level.cell;

import ch.realmtechCommuns.item.ItemType;
import ch.realmtechCommuns.mod.PlayerFootStepSound;
import ch.realmtechCommuns.mod.RealmTechCoreMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import java.util.function.BiConsumer;

public class CellBehavior {
    private ItemType breakWith;
    private float speedEffect = 1;
    private PlayerFootStepSound playerFootStepSound;
    private final byte layer;
    private BreakCell breakCellEvent;
    private int breakStepNeed = 20;
    private CreatePhysiqueBody createBody;
    private BiConsumer<com.badlogic.gdx.physics.box2d.World, Body> deleteBody;
    private BiConsumer<com.artemis.World, Integer> editEntity;
    private BiConsumer<com.artemis.World, Integer> interagieClickDroit;

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
        return breakCellEvent;
    }

    public int getBreakStepNeed() {
        return breakStepNeed;
    }

    public BiConsumer<com.artemis.World, Integer> getEditEntity() {
        return editEntity;
    }

    public CreatePhysiqueBody getCreateBody() {
        return createBody;
    }

    public BiConsumer<World, Body> getDeleteBody() {
        return deleteBody;
    }

    public BiConsumer<com.artemis.World, Integer> getInteragieClickDroit() {
        return interagieClickDroit;
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
            Gdx.app.postRunnable(() -> cellBehavior.breakCellEvent = BreakCellEvent.dropOnBreak(RealmTechCoreMod.ITEMS.get(dropItemRegistryName).getEntry()));
            return this;
        }

        public CellBehaviorBuilder dropNothing() {
            cellBehavior.breakCellEvent = new BreakCellEvent().dropNothing();
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

        public CellBehaviorBuilder editEntity(BiConsumer<com.artemis.World, Integer> editEntity) {
            cellBehavior.editEntity = editEntity;
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

        public CellBehaviorBuilder interagieClickDroit(BiConsumer<com.artemis.World, Integer> interagieClickDroit) {
            cellBehavior.interagieClickDroit = interagieClickDroit;
            return this;
        }

        public CellBehavior build() {
            return cellBehavior;
        }
    }
}
