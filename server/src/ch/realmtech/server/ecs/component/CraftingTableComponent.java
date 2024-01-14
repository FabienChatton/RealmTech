package ch.realmtech.server.ecs.component;

import ch.realmtech.server.craft.CraftResult;
import ch.realmtech.server.craft.CraftingStrategyItf;
import ch.realmtech.server.registery.CraftingRecipeEntry;
import ch.realmtech.server.registery.InfRegistryAnonymeImmutable;
import com.artemis.Component;
import com.artemis.annotations.EntityId;

import java.util.Optional;
import java.util.function.Function;

public class CraftingTableComponent extends Component {
    @EntityId
    public int craftingInventory;

    @EntityId
    public int craftingResultInventory;

    private CraftingStrategyItf craftStrategy;
    private Function<Integer, Optional<CraftResult>> canProcessCraftCraftingTable;

    private InfRegistryAnonymeImmutable<CraftingRecipeEntry> registry;

    public void set(int craftingInventory, int craftingResultInventory, CraftingStrategyItf craftStrategy, InfRegistryAnonymeImmutable<CraftingRecipeEntry> registry, Function<Integer, Optional<CraftResult>> canProcessCraftCraftingTable) {
        this.craftingInventory = craftingInventory;
        this.craftingResultInventory = craftingResultInventory;
        this.craftStrategy = craftStrategy;
        this.registry = registry;
        this.canProcessCraftCraftingTable = canProcessCraftCraftingTable;
    }

    public CraftingStrategyItf getCraftResultStrategy() {
        return craftStrategy;
    }

    public Function<Integer, Optional<CraftResult>> getCanProcessCraftCraftingTable() {
        return canProcessCraftCraftingTable;
    }

    public InfRegistryAnonymeImmutable<CraftingRecipeEntry> getRegistry() {
        return registry;
    }
}
