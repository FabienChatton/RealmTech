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
public class CraftingPlayerSystem extends IteratingSystem {
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<CraftingComponent> mCrafting;
    @Override
    protected void process(int inventoryId) {
        InventoryComponent inventoryComponent = mInventory.get(inventoryId);
        CraftingComponent craftingComponent = mCrafting.get(inventoryId);
        int[][] inventory = inventoryComponent.inventory;
        ItemRegisterEntry[][] itemRegister = new ItemRegisterEntry[inventoryComponent.numberOfSlotParRow][inventoryComponent.numberOfRow];
        // permet de transformer l'inventaire du joueur (1d) en 2d
        for (int slot = 0; slot < inventory.length; slot++) {
            if (inventory[slot][0] == 0) continue;
            ItemComponent itemComponent = mItem.get(inventory[slot][0]);
            if (itemComponent != null) {
                itemRegister[slot % inventoryComponent.numberOfSlotParRow][slot / inventoryComponent.numberOfRow] = itemComponent.itemRegisterEntry;
            }
        }
        boolean nouveauCraft = false;
        for (CraftingRecipeEntry craftingRecipeEntry : craftingComponent.craftingRecipe) {
            ItemRegisterEntry craftResult = craftingRecipeEntry.craft(itemRegister);
            if (craftResult != null) {
                world.getSystem(PlayerInventorySystem.class).nouveauCraftDisponible(craftResult);
                nouveauCraft = true;
                break;
            }
        }
        if (!nouveauCraft) {
            world.getSystem(PlayerInventorySystem.class).aucunCraftDisponible();
        }
    }
}
