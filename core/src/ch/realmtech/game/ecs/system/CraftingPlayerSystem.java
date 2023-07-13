package ch.realmtech.game.ecs.system;

import ch.realmtech.game.craft.CraftResult;
import ch.realmtech.game.ecs.component.CraftingComponent;
import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.registery.CraftingRecipeEntry;
import ch.realmtech.game.registery.ItemRegisterEntry;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

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
        ItemRegisterEntry[] itemRegister = new ItemRegisterEntry[inventory.length];
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i][0] != 0) {
                itemRegister[i] = mItem.get(inventory[i][0]).itemRegisterEntry;
            }
        }
        boolean nouveauCraft = false;
        if (Arrays.stream(itemRegister).anyMatch(Objects::nonNull)) {
            for (CraftingRecipeEntry craftingRecipeEntry : craftingComponent.craftingRecipe) {
                final Optional<CraftResult> craftResult = craftingRecipeEntry.craft(itemRegister);
                if (craftResult.isPresent()) {
                    world.getSystem(PlayerInventorySystem.class).nouveauCraftDisponible(craftResult.get(), craftingRecipeEntry);
                    nouveauCraft = true;
                    break;
                }
            }
        }
        if (!nouveauCraft) {
            world.getSystem(PlayerInventorySystem.class).aucunCraftDisponible();
        }
    }
}
