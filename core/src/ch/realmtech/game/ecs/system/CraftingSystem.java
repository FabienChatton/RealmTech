package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.CraftingComponent;
import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.registery.CraftingRecipeEntry;
import ch.realmtech.game.registery.ItemRegisterEntry;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;

@All({InventoryComponent.class, CraftingComponent.class})
public class CraftingSystem extends IteratingSystem {
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<CraftingComponent> mCrafting;
    @Override
    protected void process(int entityId) {
        InventoryComponent inventoryComponent = mInventory.get(entityId);
        CraftingComponent craftingComponent = mCrafting.get(entityId);
        int[][] inventory = inventoryComponent.inventory;
        ItemRegisterEntry[][] itemRegister = new ItemRegisterEntry[inventoryComponent.numberOfSlotParRow][inventoryComponent.numberOfRow];
        for (int slot = 0; slot < inventory.length; slot++) {
            if (inventory[slot][0] == 0) continue;
            ItemComponent itemComponent = mItem.get(inventory[slot][0]);
            if (itemComponent != null) {
                itemRegister[slot % inventoryComponent.numberOfSlotParRow][slot / inventoryComponent.numberOfRow] = itemComponent.itemRegisterEntry;
            }
        }
        for (CraftingRecipeEntry craftingRecipeEntry : craftingComponent.craftingRecipe) {
            if (craftingRecipeEntry.craft(itemRegister) != null) {
                System.out.println("fumier");
            }
        }
    }
}
