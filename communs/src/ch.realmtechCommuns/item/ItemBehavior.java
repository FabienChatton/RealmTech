package ch.realmtechCommuns.item;

import ch.realmtechCommuns.mod.RealmTechCoreMod;
import ch.realmtechCommuns.registery.CellRegisterEntry;
import com.badlogic.gdx.Gdx;

public class ItemBehavior {
    private int attackDommage;
    private ItemType itemType;
    private float speedEffect;
    private CellRegisterEntry placeCell;
    private int timeToBurn = 0;
    private boolean icon;

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

    public int getTimeToBurn() {
        return timeToBurn;
    }

    public boolean isIcon() {
        return icon;
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

        public ItemBehaviorBuilder setTimeToBurn(int timeToBurn) {
            itemBehavior.timeToBurn = timeToBurn;
            return this;
        }

        public ItemBehaviorBuilder icon() {
            itemBehavior.icon = true;
            return this;
        }

        public ItemBehavior build() {
            return itemBehavior;
        }
    }
}
