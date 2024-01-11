package ch.realmtech.server.ecs.component;

import ch.realmtech.server.craft.CraftingStrategyItf;
import ch.realmtech.server.registery.CraftingRecipeEntry;
import ch.realmtech.server.registery.InfRegistryAnonyme;
import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class CraftingTableComponent extends Component {
    @EntityId
    public int craftingInventory;

    @EntityId
    public int craftingResultInventory;

    private CraftingStrategyItf craftStrategy;

    private InfRegistryAnonyme<CraftingRecipeEntry> registry;

    public void set(int craftingInventory, int craftingResultInventory, CraftingStrategyItf craftStrategy, InfRegistryAnonyme<CraftingRecipeEntry> registry) {
        this.craftingInventory = craftingInventory;
        this.craftingResultInventory = craftingResultInventory;
        this.craftStrategy = craftStrategy;
        this.registry = registry;
    }

    public CraftingStrategyItf getCraftResultStrategy() {
        return craftStrategy;
    }

    public InfRegistryAnonyme<CraftingRecipeEntry> getRegistry() {
        return registry;
    }
}
