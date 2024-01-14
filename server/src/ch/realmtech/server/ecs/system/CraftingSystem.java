package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.craft.CraftResult;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.component.UuidComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.packet.clientPacket.InventorySetPacket;
import ch.realmtech.server.registery.CraftingRecipeEntry;
import ch.realmtech.server.registery.ItemRegisterEntry;
import ch.realmtech.server.registery.RegistryEntryAnonyme;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

import java.util.*;

@All(CraftingTableComponent.class)
public class CraftingSystem extends IteratingSystem {
    @Wire
    private SystemsAdminServer systemsAdminServer;
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<CraftingTableComponent> mCraftingTable;
    private ComponentMapper<UuidComponent> mUuid;

    @Override
    protected void process(int entityId) {
        CraftingTableComponent craftingTableComponent = mCraftingTable.get(entityId);

        Optional<CraftResult> apply = craftingTableComponent.getCanProcessCraftCraftingTable().apply(entityId);
        CraftResult craftResult = getCraft(entityId);
        if (craftingTableComponent.getCraftResultStrategy() != null) {
            if (craftingTableComponent.getCraftResultStrategy().consumeCraftingStrategy(serverContext, world, craftResult, entityId)) {
                UUID uuid = mUuid.get(craftingTableComponent.craftingResultInventory).getUuid();
                serverContext.getServerHandler().broadCastPacket(new InventorySetPacket(uuid, serverContext.getSerializerController().getInventorySerializerManager().encode(mInventory.get(craftingTableComponent.craftingResultInventory))));
            }
        }
    }

    public CraftResult getCraft(int craftingTableEntity) {
        CraftingTableComponent craftingTableComponent = mCraftingTable.get(craftingTableEntity);
        InventoryComponent inventoryCraftComponent = systemsAdminServer.inventoryManager.getCraftingInventory(craftingTableEntity);
        if (inventoryCraftComponent == null || inventoryCraftComponent.inventory == null) {
            return null;
        }
        int[][] inventoryCraft = inventoryCraftComponent.inventory;
        List<ItemRegisterEntry> itemRegister = Arrays.stream(inventoryCraft)
                .map((stack) -> mItem.get(stack[0]))
                .map((itemComponent -> itemComponent != null ? itemComponent.itemRegisterEntry : null))
                .toList();

        if (itemRegister.stream().anyMatch(Objects::nonNull)) {
            for (RegistryEntryAnonyme<CraftingRecipeEntry> craftingRecipeEntry : craftingTableComponent.getRegistry().getEnfants()) {
                Optional<CraftResult> craftResultOption = craftingRecipeEntry.getEntry().craft(itemRegister);
                if (craftResultOption.isPresent()) {
                    return craftResultOption.get();
                }
            }
        }
        return null;
    }
}
