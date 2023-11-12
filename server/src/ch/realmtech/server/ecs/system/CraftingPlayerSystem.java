package ch.realmtech.server.ecs.system;

import ch.realmtech.server.craft.CraftResult;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.CraftingComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.registery.CraftingRecipeEntry;
import ch.realmtech.server.registery.ItemRegisterEntry;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
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

        CraftResult craftResult = getCraft(craftingTableComponent);
        craftingTableComponent.getCraftResultStrategy().consumeCraftingStrategy(world, craftResult, entityId);
    }

    public CraftResult getCraft(CraftingTableComponent craftingTableComponent) {
        InventoryComponent inventoryCraftComponent = mInventory.get(craftingTableComponent.craftingInventory);
        CraftingComponent craftingComponent = mCrafting.get(craftingTableComponent.craftingInventory);
        int[][] inventoryCraft = inventoryCraftComponent.inventory;
        ItemRegisterEntry[] itemRegister = new ItemRegisterEntry[inventoryCraft.length];
        for (int i = 0; i < inventoryCraft.length; i++) {
            if (inventoryCraft[i][0] != 0) {
                itemRegister[i] = mItem.get(inventoryCraft[i][0]).itemRegisterEntry;
            }
        }

        if (Arrays.stream(itemRegister).anyMatch(Objects::nonNull)) {
            for (CraftingRecipeEntry craftingRecipeEntry : craftingComponent.craftingRecipe) {
                Optional<CraftResult> craftResultOption = craftingRecipeEntry.craft(itemRegister, inventoryCraftComponent.numberOfSlotParRow, inventoryCraftComponent.numberOfRow);
                if (craftResultOption.isPresent()) {
                    return craftResultOption.get();
                }
            }
        }
        return null;
    }
}
