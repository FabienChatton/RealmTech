package ch.realmtech.game.item;

public class ItemBehavior {
    private int attackDommage;

    private ItemBehavior() {
        attackDommage = 1;
    }

    public int getAttackDommage() {
        return attackDommage;
    }

    public static class Builder {
        private final ItemBehavior itemBehavior = new ItemBehavior();

        public Builder setAttackDommage(int attackDommage) {
            itemBehavior.attackDommage = attackDommage;
            return this;
        }

        public ItemBehavior build(){
            return itemBehavior;
        }

    }
}
