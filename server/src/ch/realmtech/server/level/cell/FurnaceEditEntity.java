package ch.realmtech.server.level.cell;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.plugin.forclient.SystemsAdminClientForClient;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.registry.CraftRecipeEntry;
import ch.realmtech.server.registry.ItemEntry;
import ch.realmtech.server.registry.RegistryUtils;
import ch.realmtech.server.uuid.UuidSupplierOrRandom;

import java.util.List;
import java.util.function.BiPredicate;

public class FurnaceEditEntity implements EditEntity {
    private final UuidSupplierOrRandom furnaceUuid;
    private final UuidSupplierOrRandom craftingInventoryUuid;
    private final int[][] craftingInventory;
    private final UuidSupplierOrRandom carburantInventoryUuid;
    private final int[][] carburantInventory;
    private final UuidSupplierOrRandom craftingResultInventoryUuid;
    private final int[][] craftingResultInventory;

    public FurnaceEditEntity(UuidSupplierOrRandom furnaceUuid, UuidSupplierOrRandom craftingInventoryUuid, int[][] craftingInventory, UuidSupplierOrRandom carburantInventoryUuid, int[][] carburantInventory, UuidSupplierOrRandom craftingResultInventoryUuid, int[][] craftingResultInventory) {
        this.furnaceUuid = furnaceUuid;
        this.craftingInventoryUuid = craftingInventoryUuid;
        this.craftingInventory = craftingInventory;
        this.carburantInventoryUuid = carburantInventoryUuid;
        this.craftingResultInventory = craftingResultInventory;
        this.craftingResultInventoryUuid = craftingResultInventoryUuid;
        this.carburantInventory = carburantInventory;
    }

    public static FurnaceEditEntity createFurnace() {
        return new FurnaceEditEntity(new UuidSupplierOrRandom(), new UuidSupplierOrRandom(), new int[1][InventoryComponent.DEFAULT_STACK_LIMITE], new UuidSupplierOrRandom(), new int[1][InventoryComponent.DEFAULT_STACK_LIMITE], new UuidSupplierOrRandom(), new int[1][InventoryComponent.DEFAULT_STACK_LIMITE]);
    }

    @Override
    public void createEntity(ExecuteOnContext executeOnContext, int entityId) {
        executeOnContext.onCommun((world) -> world.getSystem(InventoryManager.class).createFurnace(entityId, furnaceUuid.get(), craftingInventoryUuid.get(), craftingInventory, carburantInventoryUuid.get(), carburantInventory, craftingResultInventoryUuid.get(), craftingResultInventory));
        executeOnContext.onClientWorld((systemsAdminClientForClient, world) -> systemsAdminClientForClient.getFurnaceIconSystem().createIconFurnace(entityId));
    }

    @Override
    public void deleteEntity(ExecuteOnContext executeOnContext, int entityId) {
        executeOnContext.onClientWorld((systemsAdminClientForClient, world) -> systemsAdminClientForClient.getFurnaceIconSystem().deleteIconFurnace(entityId));
    }

    public static BiPredicate<SystemsAdminClientForClient, ItemEntry> testValideItemForCraft() {
        return (systemsAdminClient, itemRegisterEntry) -> {
            List<? extends CraftRecipeEntry> furnacesRecipes = RegistryUtils.findEntries(systemsAdminClient.getRootRegistry(), "#furnaceRecipes");
            return systemsAdminClient.getCraftingManager().getNewCraftResult(furnacesRecipes, List.of(List.of(itemRegisterEntry))).isPresent();
        };
    }

    public static BiPredicate<SystemsAdminClientForClient, ItemEntry> testValideItemCarburant() {
        return (systemsAdminClient, itemRegisterEntry) -> itemRegisterEntry.getItemBehavior().getTimeToBurn() > 0;
    }
}
