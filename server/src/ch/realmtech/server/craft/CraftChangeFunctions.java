package ch.realmtech.server.craft;

import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.ecs.system.CraftingManager;
import com.artemis.ComponentMapper;
import com.artemis.World;

import java.util.Optional;

public class CraftChangeFunctions {
    public static CraftChange craftResultChangeCraftingTable(World world) {
        return (craftingTableId) -> {
            SystemsAdminServer systemsAdminServer = world.getRegistered(SystemsAdminServer.class);
            ComponentMapper<CraftingTableComponent> mCraftingTable = world.getMapper(CraftingTableComponent.class);
            CraftingTableComponent craftingTableComponent = mCraftingTable.get(craftingTableId);
            InventoryComponent craftingInventoryComponent = systemsAdminServer.inventoryManager.mInventory.get(craftingTableComponent.craftingInventory);
            InventoryComponent craftingResultInventoryComponent = systemsAdminServer.inventoryManager.mInventory.get(craftingTableComponent.craftingResultInventory);

            Optional<CraftResult> craftResult = world.getSystem(CraftingManager.class).getNewCraftResult(craftingTableComponent);

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
                return Optional.of(craftResult);
            } else {
                return Optional.empty();
            }
        };
    }
}
