package ch.realmtech.server.level.cell;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.registry.CraftRecipeEntry;
import ch.realmtech.server.uuid.UuidSupplierOrRandom;
import com.badlogic.gdx.utils.Null;

import java.util.List;

public class CraftingTableEditEntity implements EditEntity {
    private final UuidSupplierOrRandom craftingInventoryUuid;
    @Null
    private final int[][] inventory;
    private final int craftingNumberOfSlotParRow;
    private final int craftingNumberOfRow;
    private final UuidSupplierOrRandom craftingResultInventoryUuid;
    private final List<? extends CraftRecipeEntry> craftRecipes;

    public CraftingTableEditEntity(UuidSupplierOrRandom craftingInventoryUuid, int[][] inventory, int craftingNumberOfSlotParRow, int craftingNumberOfRow, UuidSupplierOrRandom craftingResultInventoryUuid, List<? extends CraftRecipeEntry> craftRecipes) {
        this.craftingInventoryUuid = craftingInventoryUuid;
        this.inventory = inventory;
        this.craftingNumberOfSlotParRow = craftingNumberOfSlotParRow;
        this.craftingNumberOfRow = craftingNumberOfRow;
        this.craftingResultInventoryUuid = craftingResultInventoryUuid;
        this.craftRecipes = craftRecipes;
    }

    @Override
    public void createEntity(ExecuteOnContext executeOnContext, int entityId) {
        executeOnContext.onCommun(world -> ((SystemsAdminCommun) world.getRegistered("systemsAdmin")).getInventoryManager().createCraftingTable(entityId, craftingInventoryUuid.get(), inventory, craftingNumberOfSlotParRow, craftingNumberOfRow, craftingResultInventoryUuid.get(), craftRecipes));
    }

    @Override
    public void deleteEntity(ExecuteOnContext executeOnContext, int entityId) {
        executeOnContext.onCommun((world) -> {
            SystemsAdminCommun systemsAdmin = world.getRegistered("systemsAdmin");
            CraftingTableComponent craftingTableComponent = world.getMapper(CraftingTableComponent.class).get(entityId);
            InventoryComponent craftingInventory = systemsAdmin.getInventoryManager().mInventory.get(craftingTableComponent.craftingInventory);
            InventoryComponent resultInventory = systemsAdmin.getInventoryManager().mInventory.get(craftingTableComponent.craftingInventory);
            systemsAdmin.getInventoryManager().removeInventory(craftingInventory.inventory);
            systemsAdmin.getInventoryManager().removeInventory(resultInventory.inventory);

            systemsAdmin.getUuidEntityManager().deleteRegisteredEntity(craftingTableComponent.craftingInventory);
            systemsAdmin.getUuidEntityManager().deleteRegisteredEntity(craftingTableComponent.craftingResultInventory);
        });
    }

}
