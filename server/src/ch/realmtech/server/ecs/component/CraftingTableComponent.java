package ch.realmtech.server.ecs.component;

import ch.realmtech.server.craft.CraftResultChange;
import ch.realmtech.server.newCraft.NewCraftResult;
import ch.realmtech.server.newRegistry.NewCraftRecipeEntry;
import ch.realmtech.server.newRegistry.NewRegistry;
import com.artemis.Component;
import com.artemis.World;
import com.artemis.annotations.EntityId;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class CraftingTableComponent extends Component {
    @EntityId
    public int craftingInventory;

    @EntityId
    public int craftingResultInventory;
    private Function<Integer, Optional<CraftResultChange>> isCraftResultChange;
    private BiFunction<World, Integer, Function<CraftingTableComponent, Consumer<Optional<NewCraftResult>>>> onCraftResultChange;

    private NewRegistry<? extends NewCraftRecipeEntry> registry;

    public void set(int craftingInventory, int craftingResultInventory, NewRegistry<? extends NewCraftRecipeEntry> registry, Function<Integer, Optional<CraftResultChange>> canProcessCraftCraftingTable, BiFunction<World, Integer, Function<CraftingTableComponent, Consumer<Optional<NewCraftResult>>>> onCraftResultChange) {
        this.craftingInventory = craftingInventory;
        this.craftingResultInventory = craftingResultInventory;
        this.registry = registry;
        this.isCraftResultChange = canProcessCraftCraftingTable;
        this.onCraftResultChange = onCraftResultChange;
    }

    public Function<Integer, Optional<CraftResultChange>> getIsCraftResultChange() {
        return isCraftResultChange;
    }

    public NewRegistry<? extends NewCraftRecipeEntry> getRegistry() {
        return registry;
    }

    public Consumer<Optional<NewCraftResult>> getOnCraftResultChange(World world, int craftingTableId) {
        return onCraftResultChange.apply(world, craftingTableId).apply(this);
    }
}
