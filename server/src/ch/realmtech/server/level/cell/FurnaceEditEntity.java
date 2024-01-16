package ch.realmtech.server.level.cell;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.mod.RealmTechCoreMod;

import java.util.UUID;

public class FurnaceEditEntity implements EditEntity {
    private final UUID furnaceUuid;
    private final UUID craftingInventoryUuid;
    private final int[][] craftingInventory;
    private final UUID carburantInventoryUuid;
    private final int[][] carburantInventory;
    private final UUID craftingResultInventoryUuid;
    private final int[][] craftingResultInventory;

    public FurnaceEditEntity(UUID furnaceUuid, UUID craftingInventoryUuid, int[][] craftingInventory, UUID carburantInventoryUuid, int[][] carburantInventory, UUID craftingResultInventoryUuid, int[][] craftingResultInventory) {
        this.furnaceUuid = furnaceUuid;
        this.craftingInventoryUuid = craftingInventoryUuid;
        this.craftingInventory = craftingInventory;
        this.carburantInventoryUuid = carburantInventoryUuid;
        this.craftingResultInventory = craftingResultInventory;
        this.craftingResultInventoryUuid = craftingResultInventoryUuid;
        this.carburantInventory = carburantInventory;
    }

    public static FurnaceEditEntity createFurnace() {
        return new FurnaceEditEntity(UUID.randomUUID(), UUID.randomUUID(), new int[1][InventoryComponent.DEFAULT_STACK_LIMITE], UUID.randomUUID(), new int[1][InventoryComponent.DEFAULT_STACK_LIMITE], UUID.randomUUID(), new int[1][InventoryComponent.DEFAULT_STACK_LIMITE]);
    }

    public static EditEntity deleteIconFurnace() {
        return (executeOnContext, entityId) -> executeOnContext.onClient((systemsAdminClientForClient, world) -> systemsAdminClientForClient.getFurnaceIconSystem().deleteIconFurnace(entityId));
    }

    @Override
    public void editEntity(ExecuteOnContext executeOnContext, int entityId) {
        executeOnContext.onCommun((world) -> world.getSystem(InventoryManager.class).createFurnace(entityId, furnaceUuid, craftingInventoryUuid, craftingInventory, carburantInventoryUuid, carburantInventory, craftingResultInventoryUuid, craftingResultInventory, RealmTechCoreMod.FURNACE_RECIPE));
        executeOnContext.onClient((systemsAdminClientForClient, world) -> systemsAdminClientForClient.getFurnaceIconSystem().createIconFurnace(entityId));
    }
}
