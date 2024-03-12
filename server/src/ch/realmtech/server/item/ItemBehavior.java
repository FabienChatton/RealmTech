package ch.realmtech.server.item;

import ch.realmtech.server.level.RightClickInteractionItemClient;
import ch.realmtech.server.newRegistry.NewCellEntry;
import com.badlogic.gdx.utils.Null;

import java.util.Optional;

public class ItemBehavior {
    private int attackDommage;
    private ItemType itemType;
    private float speedEffect;
    private NewCellEntry newPlaceCellEntry;
    @Null
    private String placeCellName;
    private int timeToBurn = 0;
    private boolean icon;
    private RightClickInteractionItemClient rightClickInteractionItemClient;

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

    public NewCellEntry getNewPlaceCellEntry() {
        return newPlaceCellEntry;
    }

    public String getPlaceCellName() {
        return placeCellName;
    }

    public Optional<RightClickInteractionItemClient> getInteragieClickDroit() {
        return Optional.ofNullable(rightClickInteractionItemClient);
    }

    public int getTimeToBurn() {
        return timeToBurn;
    }

    public boolean isIcon() {
        return icon;
    }

    public void setPlaceCell(NewCellEntry newCellEntry) {
        this.newPlaceCellEntry = newCellEntry;
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

        public ItemBehaviorBuilder interagieClickDroit(RightClickInteractionItemClient rightClickInteractionItemClient) {
            itemBehavior.rightClickInteractionItemClient = rightClickInteractionItemClient;
            return this;
        }

        public ItemBehavior build() {
            return itemBehavior;
        }
    }
}
