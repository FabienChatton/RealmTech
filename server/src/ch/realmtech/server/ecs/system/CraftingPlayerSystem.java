package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.craft.CraftResult;
import ch.realmtech.server.ecs.component.*;
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
import java.util.UUID;

@All(CraftingTableComponent.class)
public class CraftingPlayerSystem extends IteratingSystem {
    @Wire
    private SystemsAdminServer systemsAdminServer;
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<CraftingComponent> mCrafting;
    private ComponentMapper<CraftingTableComponent> mCraftingTable;
    private ComponentMapper<UuidComponent> mUuid;

    @Override
    protected void process(int entityId) {
        CraftingTableComponent craftingTableComponent = mCraftingTable.get(entityId);

        CraftResult craftResult = getCraft(entityId);
        if (craftingTableComponent.getCraftResultStrategy() != null) {
            if (craftingTableComponent.getCraftResultStrategy().consumeCraftingStrategy(serverContext, world, craftResult, entityId)) {
                UUID uuid = mUuid.get(craftingTableComponent.craftingResultInventory).getUuid();
                serverContext.getSerializerController().getInventorySerializerManager().toBytesLatest(world, serverContext.getSerializerController(), mInventory.get(craftingTableComponent.craftingResultInventory));
            }
        }
    }

    public CraftResult getCraft(int craftingTableEntity) {
        CraftingTableComponent craftingTableComponent = mCraftingTable.get(craftingTableEntity);
        InventoryComponent inventoryCraftComponent = systemsAdminServer.inventoryManager.getCraftingInventory(craftingTableEntity);
        CraftingComponent craftingComponent = mCrafting.get(craftingTableComponent.craftingInventory);
        if (inventoryCraftComponent == null || inventoryCraftComponent.inventory == null) {
            return null;
        }
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
