package ch.realmtech.game.ecs.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class CraftingTableComponent extends Component {
    @EntityId
    public int craftingInventory;

    @EntityId
    public int craftingResultInventory;

    public void set(int craftingInventory, int craftingResultInventory) {
        this.craftingInventory = craftingInventory;
        this.craftingResultInventory = craftingResultInventory;
    }
}
