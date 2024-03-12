package ch.realmtech.server.ecs.system;

import ch.realmtech.server.craft.CraftResult;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.newCraft.NewCraftResult;
import ch.realmtech.server.newRegistry.NewCraftRecipeEntry;
import ch.realmtech.server.newRegistry.NewItemEntry;
import ch.realmtech.server.newRegistry.NewRegistry;
import ch.realmtech.server.newRegistry.RegistryUtils;
import ch.realmtech.server.registery.CraftingRecipeEntry;
import ch.realmtech.server.registery.InfRegistryAnonymeImmutable;
import com.artemis.Manager;
import com.artemis.annotations.Wire;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CraftingManager extends Manager {
    @Wire(name = "systemsAdmin")
    private SystemsAdminCommun systemsAdminCommun;
    // TODO mettre le registre dans la table de craft
    @Wire(name = "rootRegistry")
    private NewRegistry<?> rootRegistry;

    public Optional<NewCraftResult> getNewCraftResult(CraftingTableComponent craftingTableComponent) {
        Optional<NewRegistry<?>> craftRegistry = RegistryUtils.findRegistry(rootRegistry, "realmtech.crafts.craftingTable");
        return getNewCraftResult((NewRegistry<NewCraftRecipeEntry>) craftRegistry.get(), systemsAdminCommun.inventoryManager.mapInventoryToItemRegistry(craftingTableComponent.craftingInventory));
    }

    @Deprecated
    public Optional<CraftResult> getCraftResult(InfRegistryAnonymeImmutable<CraftingRecipeEntry> craftRegistry, List<NewItemEntry> itemInventoryRegistry) {
        return Optional.empty();
    }

    public Optional<NewCraftResult> getNewCraftResult(NewRegistry<NewCraftRecipeEntry> craftRegistry, List<NewItemEntry> itemInventoryRegistry) {
        return craftRegistry.getEntries().stream()
                .map((craftRecipeEntry) -> craftRecipeEntry.craft(itemInventoryRegistry))
                .filter(Optional::isPresent)
                .findFirst()
                .flatMap(Function.identity());
    }
}
