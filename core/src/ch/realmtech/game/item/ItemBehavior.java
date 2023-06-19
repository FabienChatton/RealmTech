package ch.realmtech.game.item;

public class ItemBehavior {
    private int attackDommage;
    private ItemType itemType;
    private float speedEffect;

    private ItemBehavior() {
        attackDommage = 1;
        speedEffect = 1;
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

    @Override
    public String toString() {
        return itemType.toString();
    }

    public static class Builder {
        private final ItemBehavior itemBehavior = new ItemBehavior();

        public Builder setAttackDommage(int attackDommage) {
            itemBehavior.attackDommage = attackDommage;
            return this;
        }

        public Builder setItemType(ItemType itemType) {
            itemBehavior.itemType = itemType;
            return this;
        }
        public Builder setSpeedEffect(float speedEffect) {
            itemBehavior.speedEffect = speedEffect;
            return this;
        }

        public ItemBehavior build(){
            return itemBehavior;
        }
    }
}