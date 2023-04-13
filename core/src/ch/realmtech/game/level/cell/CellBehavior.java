package ch.realmtech.game.level.cell;

import ch.realmtech.game.item.ItemType;

public class CellBehavior {

    private ItemType breakWith;
    private float speedEffect = 1;

    private CellBehavior() {}

    public ItemType getBreakWith() {
        return breakWith;
    }

    public float getSpeedEffect() {
        return speedEffect;
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

        public CellBehavior build(){
            return cellBehavior;
        }
    }
}
