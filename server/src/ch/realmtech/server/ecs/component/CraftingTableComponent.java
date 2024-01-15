package ch.realmtech.server.ecs.component;

import ch.realmtech.server.craft.CraftResult;
import ch.realmtech.server.craft.CraftResultChange;
import ch.realmtech.server.craft.CraftingStrategyItf;
import ch.realmtech.server.craft.OnNewCraftAvailable;
import ch.realmtech.server.registery.CraftingRecipeEntry;
import ch.realmtech.server.registery.InfRegistryAnonymeImmutable;
import com.artemis.Component;
import com.artemis.World;
import com.artemis.annotations.EntityId;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class CraftingTableComponent extends Component {
    @EntityId
    public int craftingInventory;

    @EntityId
    public int craftingResultInventory;

    @Deprecated
    private CraftingStrategyItf craftStrategy;
    private Function<Integer, Optional<CraftResultChange>> isCraftResultChange;
    private Function<World, Function<CraftingTableComponent, Consumer<Optional<CraftResult>>>> onCraftResultChange;

    private InfRegistryAnonymeImmutable<CraftingRecipeEntry> registry;

    public void set(int craftingInventory, int craftingResultInventory, CraftingStrategyItf craftStrategy, InfRegistryAnonymeImmutable<CraftingRecipeEntry> registry, Function<Integer, Optional<CraftResultChange>> canProcessCraftCraftingTable) {
        this.craftingInventory = craftingInventory;
        this.craftingResultInventory = craftingResultInventory;
        this.craftStrategy = craftStrategy;
        this.registry = registry;
        this.isCraftResultChange = canProcessCraftCraftingTable;
        onCraftResultChange = OnNewCraftAvailable.onNewCraftAvailableCraftingTable();
    }

    @Deprecated
    public CraftingStrategyItf getCraftResultStrategy() {
        return craftStrategy;
    }

    public Function<Integer, Optional<CraftResultChange>> getIsCraftResultChange() {
        return isCraftResultChange;
    }

    public InfRegistryAnonymeImmutable<CraftingRecipeEntry> getRegistry() {
        return registry;
    }

    public Consumer<Optional<CraftResult>> getOnCraftResultChange(World world) {
        return onCraftResultChange.apply(world).apply(this);
    }
}
