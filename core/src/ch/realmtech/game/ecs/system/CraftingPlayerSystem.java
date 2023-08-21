package ch.realmtech.game.ecs.system;

import ch.realmtech.game.craft.CraftResult;
import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.item.ItemResultCraftPickEvent;
import ch.realmtech.game.registery.CraftingRecipeEntry;
import ch.realmtech.game.registery.ItemRegisterEntry;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@All(CraftingTableComponent.class)
public class CraftingPlayerSystem extends IteratingSystem {
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<CraftingComponent> mCrafting;
    private ComponentMapper<CraftingTableComponent> mCraftingTable;

    @Override
    protected void process(int entityId) {
        CraftingTableComponent craftingTableComponent = mCraftingTable.get(entityId);
        if (!craftingTableComponent.getCanCraft()) {
            return;
        }
        InventoryComponent inventoryCraftComponent = mInventory.get(craftingTableComponent.craftingInventory);
        CraftingComponent craftingComponent = mCrafting.get(craftingTableComponent.craftingInventory);
        InventoryComponent inventoryResultComponent = mInventory.get(craftingTableComponent.craftingResultInventory);
        int[][] inventoryCraft = inventoryCraftComponent.inventory;
        ItemRegisterEntry[] itemRegister = new ItemRegisterEntry[inventoryCraft.length];
        for (int i = 0; i < inventoryCraft.length; i++) {
            if (inventoryCraft[i][0] != 0) {
                itemRegister[i] = mItem.get(inventoryCraft[i][0]).itemRegisterEntry;
            }
        }
//        boolean craftDisponible = false;
        if (Arrays.stream(itemRegister).anyMatch(Objects::nonNull)) {
            for (CraftingRecipeEntry craftingRecipeEntry : craftingComponent.craftingRecipe) {
                Optional<CraftResult> craftResultOption = craftingRecipeEntry.craft(itemRegister, inventoryCraftComponent.numberOfSlotParRow, inventoryCraftComponent.numberOfRow);
                if (craftResultOption.isPresent()) {
                    CraftResult craftResult = craftResultOption.get();
                    boolean modifierStackItemResult = false;
                    if (InventoryManager.tailleStack(inventoryResultComponent.inventory[0]) == 0) {
                        modifierStackItemResult = true;
                    } else {
                        ItemComponent itemComponentResultDeja = mItem.get(inventoryResultComponent.inventory[0][0]);
                        if (craftResult.getItemRegisterEntry() != itemComponentResultDeja.itemRegisterEntry) {
                            modifierStackItemResult = true;
                        }
                    }
                    if (modifierStackItemResult) {
                        world.getSystem(InventoryManager.class).removeInventory(inventoryResultComponent.inventory);
                        for (int i = 0; i < craftResult.getNombreResult(); i++) {
                            int nouvelItemResult = world.getSystem(ItemManager.class).newItemInventory(craftResult.getItemRegisterEntry());
                            world.edit(nouvelItemResult).create(ItemResultCraftComponent.class).set(ItemResultCraftPickEvent.removeAllOneItem(inventoryCraftComponent), craftingRecipeEntry);
                            world.getSystem(InventoryManager.class).addItemToStack(inventoryResultComponent.inventory[0], nouvelItemResult);
                        }
                    }
                    return;
                }
            }
        }
        world.getSystem(InventoryManager.class).removeInventory(inventoryResultComponent.inventory);
    }

}
