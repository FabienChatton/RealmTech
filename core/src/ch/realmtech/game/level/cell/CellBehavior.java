package ch.realmtech.game.level.cell;

import ch.realmtech.RealmTech;
import ch.realmtech.game.item.ItemType;
import ch.realmtech.game.mod.PlayerFootStepSound;
import ch.realmtech.game.registery.ItemRegisterEntry;
import ch.realmtech.helper.SetContext;
import com.badlogic.gdx.audio.Sound;

import static ch.realmtech.game.level.cell.Cells.Layer;

public class CellBehavior implements SetContext {
    public static RealmTech context;
    private ItemType breakWith;
    private float speedEffect = 1;
    private PlayerFootStepSound playerFootStepSound;
    private byte layer;
    private BreakCell breakCellEvent;

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

        public Builder breakWith(ItemType itemType, ItemRegisterEntry dropItemOnBreak) {
            breakWith(itemType);
            dropOnBreak(dropItemOnBreak);
            return this;
        }

        public Builder dropOnBreak(ItemRegisterEntry itemDrop) {
            cellBehavior.breakCellEvent = BreakCellEvent.dropOnBreak(itemDrop);
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
        public Builder playerWalkSound(Sound soundEffect, float volume) {
            cellBehavior.playerFootStepSound = new PlayerFootStepSound(soundEffect, volume);
            return this;
        }

        public Builder playerWalkSound(String soundEffectName, float volume) {
            cellBehavior.playerFootStepSound = new PlayerFootStepSound(context.getAssetManager().get(soundEffectName, Sound.class), volume);
            return this;
        }

        public CellBehavior build(){
            return cellBehavior;
        }
    }
}
