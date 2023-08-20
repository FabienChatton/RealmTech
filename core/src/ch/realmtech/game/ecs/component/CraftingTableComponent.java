package ch.realmtech.game.ecs.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

import java.util.function.Supplier;

public class CraftingTableComponent extends Component {
    @EntityId
    public int craftingInventory;

    @EntityId
    public int craftingResultInventory;

    private Supplier<Boolean> canCraft;
    private boolean consumeItemOnNewCraft;

    public void set(int craftingInventory, int craftingResultInventory, Supplier<Boolean> canCraft, boolean consumeItemOnNewCraft) {
        this.craftingInventory = craftingInventory;
        this.craftingResultInventory = craftingResultInventory;
        this.canCraft = canCraft;
        this.consumeItemOnNewCraft = consumeItemOnNewCraft;
    }

    public boolean getCanCraft() {
        return canCraft.get();
    }

    public boolean isConsumeItemOnNewCraft() {
        return consumeItemOnNewCraft;
    }
}
