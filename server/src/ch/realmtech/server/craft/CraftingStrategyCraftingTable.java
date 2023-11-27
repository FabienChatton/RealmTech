package ch.realmtech.server.craft;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.component.ItemResultCraftComponent;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.ecs.system.ItemManagerServer;
import ch.realmtech.server.item.ItemResultCraftPickEvent;
import com.artemis.ComponentMapper;
import com.artemis.World;

import java.util.UUID;

public class CraftingStrategyCraftingTable implements CraftingStrategyItf {

    @Override
    public boolean consumeCraftingStrategy(ServerContext serverContext, World world, CraftResult craftResult, int id) {
        ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);
        ComponentMapper<CraftingTableComponent> mCraftingTable = world.getMapper(CraftingTableComponent.class);
        ComponentMapper<ItemComponent> mItem = world.getMapper(ItemComponent.class);
        CraftingTableComponent craftingTableComponent = mCraftingTable.get(id);
        InventoryComponent inventoryResultComponent = mInventory.get(craftingTableComponent.craftingResultInventory);
        if (inventoryResultComponent == null) return false;
        if (craftResult != null) {
            if (isModifierStackItemResultCraftingTable(craftResult, mItem, inventoryResultComponent)) {
                world.getSystem(InventoryManager.class).removeInventory(inventoryResultComponent.inventory);
                for (int i = 0; i < craftResult.getNombreResult(); i++) {
                    int nouvelItemResult = world.getSystem(ItemManagerServer.class).newItemInventory(craftResult.getItemRegisterEntry(), UUID.randomUUID());
                    world.edit(nouvelItemResult).create(ItemResultCraftComponent.class).set(ItemResultCraftPickEvent.removeAllOneItem(craftingTableComponent.craftingInventory));
                    world.getSystem(InventoryManager.class).addItemToStack(inventoryResultComponent.inventory[0], nouvelItemResult);
                }
                return true;
            }
        } else {
            if (InventoryManager.tailleStack(inventoryResultComponent.inventory[0]) > 0) {
                world.getSystem(InventoryManager.class).removeInventory(inventoryResultComponent.inventory);
                return true;
            }
        }
        return false;
    }

    @Override
    public byte getId() {
        return 1;
    }

    private boolean isModifierStackItemResultCraftingTable(CraftResult craftResult, ComponentMapper<ItemComponent> mItem, InventoryComponent inventoryResultComponent) {
        boolean modifierStackItemResult = false;
        if (InventoryManager.tailleStack(inventoryResultComponent.inventory[0]) == 0) {
            modifierStackItemResult = true;
        } else {
            ItemComponent itemComponentResultDeja = mItem.get(inventoryResultComponent.inventory[0][0]);
            if (craftResult.getItemRegisterEntry() != itemComponentResultDeja.itemRegisterEntry) {
                modifierStackItemResult = true;
            }
        }
        return modifierStackItemResult;
    }
}
