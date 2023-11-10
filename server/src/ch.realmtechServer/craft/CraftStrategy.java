package ch.realmtechServer.craft;

import ch.realmtechServer.ecs.component.*;
import ch.realmtechServer.ecs.system.InventoryManager;
import ch.realmtechServer.ecs.system.ItemManagerServer;
import ch.realmtechServer.item.ItemResultCraftPickEvent;
import com.artemis.ComponentMapper;
import com.artemis.World;

public interface CraftStrategy {
    void consumeCraftingStrategy(World world, CraftResult craftResult, int id);

    static CraftStrategy craftingStrategyCraftingTable() {
        return (world, craftResult, id) -> {
            ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);
            ComponentMapper<CraftingTableComponent> mCraftingTable = world.getMapper(CraftingTableComponent.class);
            ComponentMapper<ItemComponent> mItem = world.getMapper(ItemComponent.class);
            CraftingTableComponent craftingTableComponent = mCraftingTable.get(id);
            if (craftResult != null) {
                InventoryComponent inventoryResultComponent = mInventory.get(craftingTableComponent.craftingResultInventory);
                InventoryComponent inventoryCraftComponent = mInventory.get(craftingTableComponent.craftingInventory);
                if (isModifierStackItemResultCraftingTable(craftResult, mItem, inventoryResultComponent)) {
                    world.getSystem(InventoryManager.class).removeInventory(inventoryResultComponent.inventory);
                    for (int i = 0; i < craftResult.getNombreResult(); i++) {
                        int nouvelItemResult = world.getSystem(ItemManagerServer.class).newItemInventory(craftResult.getItemRegisterEntry());
                        world.edit(nouvelItemResult).create(ItemResultCraftComponent.class).set(ItemResultCraftPickEvent.removeAllOneItem(inventoryCraftComponent));
                        world.getSystem(InventoryManager.class).addItemToStack(inventoryResultComponent.inventory[0], nouvelItemResult);
                    }
                }
            } else {
                world.getSystem(InventoryManager.class).removeInventory(mInventory.get(craftingTableComponent.craftingResultInventory).inventory);
            }
        };
    }

    static CraftStrategy craftingStrategyFurnace() {
        return (world, craftResult, id) -> {
            ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);
            ComponentMapper<CraftingTableComponent> mCraftingTable = world.getMapper(CraftingTableComponent.class);
            ComponentMapper<ItemComponent> mItem = world.getMapper(ItemComponent.class);
            ComponentMapper<FurnaceComponent> mFurnace = world.getMapper(FurnaceComponent.class);

            FurnaceComponent furnaceComponent = mFurnace.get(id);
            CraftingTableComponent craftingTableComponent = mCraftingTable.get(id);
            InventoryComponent inventoryCraftComponent = mInventory.get(craftingTableComponent.craftingInventory);
            InventoryComponent inventoryResultComponent = mInventory.get(craftingTableComponent.craftingResultInventory);

            if (furnaceComponent.timeToBurn == 0) {
                InventoryComponent inventoryCarburant = mInventory.get(furnaceComponent.inventoryCarburant);
                int[] stack = inventoryCarburant.inventory[0];
                int itemCarburant = InventoryManager.getTopItem(stack);
                if (mItem.has(itemCarburant)) {
                    ItemComponent itemCarburantComponent = mItem.get(itemCarburant);
                    int timeToBurn = itemCarburantComponent.itemRegisterEntry.getItemBehavior().getTimeToBurn();
                    if (timeToBurn > 0) {
                        furnaceComponent.timeToBurn = timeToBurn;
                        furnaceComponent.itemBurn = itemCarburantComponent.itemRegisterEntry;
                        world.getSystem(InventoryManager.class).deleteOneItem(stack);
                    }
                }
            } else {
                if (furnaceComponent.timeToBurn > 0) {
                    furnaceComponent.timeToBurn--;
                }
            }

            if (craftResult != null) {
                if (furnaceComponent.timeToBurn > 0 && furnaceComponent.curentBurnTime == 0 && furnaceComponent.curentCraftResult == null && isModifierStackItemResultFurnace(craftResult, mItem, inventoryResultComponent)) {
                    world.getSystem(InventoryManager.class).deleteOneItem(inventoryCraftComponent.inventory[0]);
                    furnaceComponent.curentCraftResult = craftResult;
                }
            }
            if (furnaceComponent.timeToBurn > 0) {
                if (furnaceComponent.curentCraftResult != null) {
                    if (++furnaceComponent.curentBurnTime >= furnaceComponent.curentCraftResult.getTimeToProcess()) {
                        int newItemId = world.getSystem(ItemManagerServer.class).newItemInventory(furnaceComponent.curentCraftResult.getItemRegisterEntry());
                        world.getSystem(InventoryManager.class).addItemToStack(inventoryResultComponent.inventory[0], newItemId);
                        furnaceComponent.curentBurnTime = 0;
                        furnaceComponent.curentCraftResult = null;
                    }
                }
            }
        };
    }

    private static boolean isModifierStackItemResultFurnace(CraftResult craftResult, ComponentMapper<ItemComponent> mItem, InventoryComponent inventoryResultComponent) {
        boolean modifierStackItemResult = false;
        if (InventoryManager.tailleStack(inventoryResultComponent.inventory[0]) == 0) {
            modifierStackItemResult = true;
        } else {
            ItemComponent itemComponentResultDeja = mItem.get(inventoryResultComponent.inventory[0][0]);
            if (craftResult.getItemRegisterEntry() == itemComponentResultDeja.itemRegisterEntry) {
                modifierStackItemResult = true;
            }
        }
        return modifierStackItemResult;
    }

    private static boolean isModifierStackItemResultCraftingTable(CraftResult craftResult, ComponentMapper<ItemComponent> mItem, InventoryComponent inventoryResultComponent) {
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
