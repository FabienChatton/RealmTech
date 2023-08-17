package ch.realmtech.game.level.cell;

import ch.realmtech.game.item.ItemType;
import ch.realmtech.game.mod.PlayerFootStepSound;
import ch.realmtech.game.mod.RealmTechCoreMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import java.util.function.BiConsumer;

import static ch.realmtech.game.level.cell.Cells.Layer;
import static ch.realmtech.game.level.cell.CreatePhysiqueBody.CreatePhysiqueBodyArgs;

public class CellBehavior {
    private ItemType breakWith;
    private float speedEffect = 1;
    private PlayerFootStepSound playerFootStepSound;
    private final byte layer;
    private BreakCell breakCellEvent;
    private int breakStepNeed = 20;
    private CreatePhysiqueBody createBody;
    private BiConsumer<World, Body> deleteBody;

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

    public CreatePhysiqueBody getCreateBody() {
        return createBody;
    }

    public BiConsumer<World, Body> getDeleteBody() {
        return deleteBody;
    }

    public static class Builder {
        private final CellBehavior cellBehavior;

        public Builder(byte layer) {
            cellBehavior = new CellBehavior(layer);
        }

        public Builder(Layer ground) {
            this(ground.layer);
        }

        public Builder breakWith(ItemType itemType) {
            cellBehavior.breakWith = itemType;
            return this;
        }

        public Builder breakWith(ItemType itemType, String dropItemRegistryName) {
            breakWith(itemType);
            dropOnBreak(dropItemRegistryName);
            return this;
        }

        public Builder dropOnBreak(String dropItemRegistryName) {
            Gdx.app.postRunnable(() -> cellBehavior.breakCellEvent = BreakCellEvent.dropOnBreak(RealmTechCoreMod.ITEMS.get(dropItemRegistryName).getEntry()));
            return this;
        }

        public Builder dropNothing() {
            cellBehavior.breakCellEvent = new BreakCellEvent().dropNothing();
            return this;
        }

        public Builder speedEffect(float speedEffect) {
            cellBehavior.speedEffect = speedEffect;
            return this;
        }

        public Builder playerWalkSound(String soundEffectName, float volume) {
            cellBehavior.playerFootStepSound = new PlayerFootStepSound(soundEffectName, volume);
            return this;
        }

        /**
         * Par défaut, 20
         */
        public Builder breakStepNeed(int steepNeed) {
            cellBehavior.breakStepNeed = steepNeed;
            return this;
        }

        public Builder physiqueBody(CreatePhysiqueBodyArgs createPhysiqueBody) {
            cellBehavior.createBody = createPhysiqueBody.createPhysiqueBody();
            cellBehavior.deleteBody = createPhysiqueBody.deletePhysiqueBody();
            return this;
        }

        public CellBehavior build() {
            return cellBehavior;
        }
    }
}
