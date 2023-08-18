package ch.realmtech.game.item;

import ch.realmtech.game.mod.RealmTechCoreMod;
import ch.realmtech.game.registery.CellRegisterEntry;
import com.badlogic.gdx.Gdx;

public class ItemBehavior {
    private int attackDommage;
    private ItemType itemType;
    private float speedEffect;
    private CellRegisterEntry placeCell;

    private ItemBehavior() {
        attackDommage = 1;
        speedEffect = 1;
    }

    public static ItemBehaviorBuilder builder() {
        return new ItemBehaviorBuilder();
    }

    public ItemType getItemType() {
        return itemType;
    }

    public int getAttackDommage() {
        return attackDommage;
    }

    public float getSpeedEffect() {
        return speedEffect;
    }

    public CellRegisterEntry getPlaceCell() {
        return placeCell;
    }

    public static class ItemBehaviorBuilder {
        private final ItemBehavior itemBehavior = new ItemBehavior();

        public ItemBehaviorBuilder setAttackDommage(int attackDommage) {
            itemBehavior.attackDommage = attackDommage;
            return this;
        }

        public ItemBehaviorBuilder setItemType(ItemType itemType) {
            itemBehavior.itemType = itemType;
            return this;
        }

        public ItemBehaviorBuilder setSpeedEffect(float speedEffect) {
            itemBehavior.speedEffect = speedEffect;
            return this;
        }

        public ItemBehaviorBuilder placeCell(String cellRegistryName) {
            Gdx.app.postRunnable(() -> itemBehavior.placeCell = RealmTechCoreMod.CELLS.get(cellRegistryName).getEntry());
            return this;
        }

        public ItemBehavior build(){
            return itemBehavior;
        }
    }
}
