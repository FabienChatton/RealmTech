package ch.realmtech.server.ecs.system;

import ch.realmtech.server.craft.CraftResult;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.registry.CraftRecipeEntry;
import ch.realmtech.server.registry.ItemEntry;
import ch.realmtech.server.registry.RegistryUtils;
import com.artemis.Manager;
import com.artemis.annotations.Wire;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CraftingManager extends Manager {
    @Wire(name = "systemsAdmin")
    private SystemsAdminCommun systemsAdminCommun;

    @SuppressWarnings("unchecked")
    public Optional<CraftResult> getNewCraftResult(CraftingTableComponent craftingTableComponent) {
        List<CraftRecipeEntry> craftingTableRecipes = (List<CraftRecipeEntry>) RegistryUtils.findEntries(systemsAdminCommun.rootRegistry, "#craftingTableRecipes");
        return getNewCraftResult(craftingTableRecipes, systemsAdminCommun.inventoryManager.mapInventoryToItemRegistry(craftingTableComponent.craftingInventory));
    }

    public Optional<CraftResult> getNewCraftResult(List<CraftRecipeEntry> craftEntries, List<ItemEntry> itemInventoryRegistry) {
        return craftEntries.stream()
                .map((craftEntry) -> craftEntry.craft(itemInventoryRegistry))
                .filter(Optional::isPresent)
                .findFirst()
                .flatMap(Function.identity());
    }
}
