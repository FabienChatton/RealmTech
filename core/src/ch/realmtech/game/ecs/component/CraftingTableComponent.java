package ch.realmtech.game.ecs.component;

import ch.realmtech.game.craft.CraftStrategy;
import com.artemis.Component;
import com.artemis.annotations.EntityId;

import java.util.function.Supplier;

public class CraftingTableComponent extends Component {
    @EntityId
    public int craftingInventory;

    @EntityId
    public int craftingResultInventory;

    private Supplier<Boolean> canCraft;
    private CraftStrategy craftStrategy;

    public void set(int craftingInventory, int craftingResultInventory, Supplier<Boolean> canCraft, CraftStrategy craftStrategy) {
        this.craftingInventory = craftingInventory;
        this.craftingResultInventory = craftingResultInventory;
        this.canCraft = canCraft;
        this.craftStrategy = craftStrategy;
    }

    public boolean getCanCraft() {
        return canCraft.get();
    }

    public CraftStrategy getCraftResultStrategy() {
        return craftStrategy;
    }
}
