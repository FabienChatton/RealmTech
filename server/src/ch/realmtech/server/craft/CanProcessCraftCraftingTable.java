package ch.realmtech.server.craft;

import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import com.artemis.ComponentMapper;
import com.artemis.World;

import java.util.function.Function;

public class CanProcessCraftCraftingTable {
    public static Function<Integer, Boolean> canProcessCraftCraftingTable(World world, SystemsAdminServer systemsAdminServer) {
        return (craftingTableId) -> {
            ComponentMapper<CraftingTableComponent> mCraftingTable = world.getMapper(CraftingTableComponent.class);
            CraftingTableComponent craftingTableComponent = mCraftingTable.get(craftingTableId);
            InventoryComponent craftingInventoryComponent = systemsAdminServer.inventoryManager.mInventory.get(craftingTableComponent.craftingInventory);
            InventoryComponent craftingResultInventoryComponent = systemsAdminServer.inventoryManager.mInventory.get(craftingTableComponent.craftingResultInventory);

            return false;
        };
    }
}
