package ch.realmtech.server.ecs.system;

import ch.realmtech.server.craft.CraftResult;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.registry.CraftRecipeEntry;
import ch.realmtech.server.registry.ItemEntry;
import com.artemis.Manager;
import com.artemis.annotations.Wire;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CraftingManager extends Manager {
    @Wire(name = "systemsAdmin")
    private SystemsAdminCommun systemsAdminCommun;

    public Optional<CraftResult> getNewCraftResult(CraftingTableComponent craftingTableComponent) {
        return getNewCraftResult(craftingTableComponent.getCraftRecipes(), systemsAdminCommun.getInventoryManager().mapInventoryToItemRegistry(craftingTableComponent.craftingInventory));
    }

    public Optional<CraftResult> getNewCraftResult(List<? extends CraftRecipeEntry> craftEntries, List<List<ItemEntry>> items) {
        return craftEntries.stream()
                .map((craftRecipe) -> craftRecipe.craft(items))
                .filter(Optional::isPresent)
                .findFirst()
                .flatMap(Function.identity());
    }
}
