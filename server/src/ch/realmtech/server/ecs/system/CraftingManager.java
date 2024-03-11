package ch.realmtech.server.ecs.system;

import ch.realmtech.server.craft.CraftResult;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.newRegistry.NewItemEntry;
import ch.realmtech.server.registery.CraftingRecipeEntry;
import ch.realmtech.server.registery.InfRegistryAnonymeImmutable;
import com.artemis.Manager;
import com.artemis.annotations.Wire;

import java.util.List;
import java.util.Optional;

public class CraftingManager extends Manager {
    @Wire(name = "systemsAdmin")
    private SystemsAdminCommun systemsAdminCommun;
    public Optional<CraftResult> getCraftResult(CraftingTableComponent craftingTableComponent) {
        return getCraftResult(craftingTableComponent.getRegistry(), systemsAdminCommun.inventoryManager.mapInventoryToItemRegistry(craftingTableComponent.craftingInventory));
    }

    public Optional<CraftResult> getCraftResult(InfRegistryAnonymeImmutable<CraftingRecipeEntry> craftRegistry, List<NewItemEntry> itemInventoryRegistry) {
        List<CraftResult> craftResults = craftRegistry.getEnfants().stream()
                .map((craftingRecipe) -> craftingRecipe.getEntry().craft(itemInventoryRegistry))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        Optional<CraftResult> craftResult;
        if (!craftResults.isEmpty()) {
            craftResult = Optional.of(craftResults.get(0));
        } else {
            craftResult = Optional.empty();
        }
        return craftResult;
    }
}
