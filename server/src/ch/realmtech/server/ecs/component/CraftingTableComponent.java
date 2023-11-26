package ch.realmtech.server.ecs.component;

import ch.realmtech.server.craft.CraftingStrategyItf;
import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class CraftingTableComponent extends Component {
    @EntityId
    public int craftingInventory;

    @EntityId
    public int craftingResultInventory;

    private CraftingStrategyItf craftStrategy;

    public void set(int craftingInventory, int craftingResultInventory, CraftingStrategyItf craftStrategy) {
        this.craftingInventory = craftingInventory;
        this.craftingResultInventory = craftingResultInventory;
        this.craftStrategy = craftStrategy;
    }

    public CraftingStrategyItf getCraftResultStrategy() {
        return craftStrategy;
    }
}
