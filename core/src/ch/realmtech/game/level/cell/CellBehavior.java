package ch.realmtech.game.level.cell;

import ch.realmtech.game.item.ItemType;
import ch.realmtech.game.mod.PlayerFootStepSound;
import com.badlogic.gdx.audio.Sound;

import static ch.realmtech.game.level.cell.Cells.Layer;

public class CellBehavior {

    private ItemType breakWith;
    private float speedEffect = 1;
    private PlayerFootStepSound playerFootStepSound;
    private byte layer;

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

    public static class Builder {
        private final CellBehavior cellBehavior;

        public Builder(byte layer) {
            cellBehavior = new CellBehavior(layer);
        }

        public Builder(Layer ground) {
            this(ground.layer);
        }

        public Builder breakWith(ItemType itemType){
            cellBehavior.breakWith = itemType;
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

        public CellBehavior build(){
            return cellBehavior;
        }
    }
}
