package ch.realmtech.server.ecs.component;

import ch.realmtech.server.registery.CraftingRecipeEntry;
import ch.realmtech.server.registery.InfRegistryAnonyme;
import com.artemis.Component;
import com.artemis.annotations.EntityId;


public class CraftingComponent extends Component {
    private InfRegistryAnonyme<CraftingRecipeEntry> registry;
    @EntityId
    public int resultInventory;

    public void set(InfRegistryAnonyme<CraftingRecipeEntry> registry, int resultInventory) {
        this.registry = registry;
        this.resultInventory = resultInventory;
    }

    public InfRegistryAnonyme<CraftingRecipeEntry> getRegistry() {
        return registry;
    }
}
