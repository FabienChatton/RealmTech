package ch.realmtech.server.item;

import ch.realmtech.server.registry.CellEntry;
import com.badlogic.gdx.utils.Null;

public class ItemBehavior {
    private int attackDommage;
    private float attackRange;
    private boolean fireArme;
    private ItemType itemType;
    private float speedEffect;
    private CellEntry newPlaceCellEntry;
    @Null
    private String placeCellName;
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

    public float getAttackRange() {
        return attackRange;
    }

    public boolean isFireArme() {
        return fireArme;
    }

    public float getSpeedEffect() {
        return speedEffect;
    }

    public CellEntry getNewPlaceCellEntry() {
        return newPlaceCellEntry;
    }

    public String getPlaceCellName() {
        return placeCellName;
    }

    public int getTimeToBurn() {
        return timeToBurn;
    }

    public boolean isIcon() {
        return icon;
    }

    public void setPlaceCell(CellEntry cellEntry) {
        this.newPlaceCellEntry = cellEntry;
    }

    public static class ItemBehaviorBuilder {

        private final ItemBehavior itemBehavior = new ItemBehavior();

        public ItemBehaviorBuilder setAttackDommage(int attackDommage) {
            itemBehavior.attackDommage = attackDommage;
            return this;
        }

        public ItemBehaviorBuilder setAttackRange(float attackRange) {
            itemBehavior.attackRange = attackRange;
            return this;
        }

        public ItemBehaviorBuilder setFireArm() {
            itemBehavior.fireArme = true;
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
            itemBehavior.placeCellName = cellRegistryName;
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
