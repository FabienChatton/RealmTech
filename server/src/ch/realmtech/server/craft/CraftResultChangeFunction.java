package ch.realmtech.server.craft;

import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.registery.ItemRegisterEntry;
import com.artemis.ComponentMapper;
import com.artemis.World;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class CraftResultChangeFunction {
    public static Function<Integer, Optional<CraftResultChange>> CraftResultChangeCraftingTable(World world) {
        return (craftingTableId) -> {
            SystemsAdminServer systemsAdminServer = world.getRegistered(SystemsAdminServer.class);
            ComponentMapper<CraftingTableComponent> mCraftingTable = world.getMapper(CraftingTableComponent.class);
            CraftingTableComponent craftingTableComponent = mCraftingTable.get(craftingTableId);
            InventoryComponent craftingInventoryComponent = systemsAdminServer.inventoryManager.mInventory.get(craftingTableComponent.craftingInventory);
            InventoryComponent craftingResultInventoryComponent = systemsAdminServer.inventoryManager.mInventory.get(craftingTableComponent.craftingResultInventory);

            List<ItemRegisterEntry> itemRegisterEntries = Arrays.stream(craftingInventoryComponent.inventory)
                    .map((stack) -> systemsAdminServer.inventoryManager.mItem.get(stack[0]))
                    .map((itemComponent) -> itemComponent != null ? itemComponent.itemRegisterEntry : null)
                    .toList();

            List<CraftResult> craftResults = craftingTableComponent.getRegistry().getEnfants().stream()
                    .map((craftingRecipe) -> craftingRecipe.getEntry().craft(itemRegisterEntries))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

            Optional<CraftResult> craftResult;
            if (!craftResults.isEmpty()) {
                craftResult = Optional.of(craftResults.get(0));
            } else {
                craftResult = Optional.empty();
            }

            boolean canProcessCraft = false;
            ItemComponent itemDejaResultComponent = systemsAdminServer.inventoryManager.mItem.get(craftingResultInventoryComponent.inventory[0][0]);
            if (craftResult.isPresent()) {
                // new craft available
                if (itemDejaResultComponent == null) {
                    canProcessCraft = true;
                }
            } else {
                // craft to remove
                if (itemDejaResultComponent != null) {
                    canProcessCraft = true;
                }
            }
            if (canProcessCraft) {
                return Optional.of(new CraftResultChange(craftResult));
            } else {
                return Optional.empty();
            }
        };
    }
}
