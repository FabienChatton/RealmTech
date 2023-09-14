package ch.realmtechCommuns.ecs.component;

import ch.realmtechCommuns.craft.CraftStrategy;
import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class CraftingTableComponent extends Component {
    @EntityId
    public int craftingInventory;

    @EntityId
    public int craftingResultInventory;

    private CraftStrategy craftStrategy;

    public void set(int craftingInventory, int craftingResultInventory, CraftStrategy craftStrategy) {
        this.craftingInventory = craftingInventory;
        this.craftingResultInventory = craftingResultInventory;
        this.craftStrategy = craftStrategy;
    }

    public CraftStrategy getCraftResultStrategy() {
        return craftStrategy;
    }
}
