package ch.realmtech.game.level.cell;

import ch.realmtech.game.item.ItemType;
import ch.realmtech.game.mod.PlayerFootStepSound;
import com.badlogic.gdx.audio.Sound;

public class CellBehavior {

    private ItemType breakWith;
    private float speedEffect = 1;
    private PlayerFootStepSound playerFootStepSound;

    private CellBehavior() {}

    public ItemType getBreakWith() {
        return breakWith;
    }

    public float getSpeedEffect() {
        return speedEffect;
    }

    public PlayerFootStepSound getPlayerFootStepSound() {
        return playerFootStepSound;
    }

    public static class Builder {
        private final CellBehavior cellBehavior = new CellBehavior();
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
