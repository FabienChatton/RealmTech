package ch.realmtech.server.level.cell;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.mod.RealmTechCoreMod;
import com.badlogic.gdx.utils.Null;

import java.util.UUID;

public class CraftingTableEditEntity implements EditEntity {
    private final UUID craftingInventoryUuid;
    @Null
    private final int[][] inventory;
    private final int craftingNumberOfSlotParRow;
    private final int craftingNumberOfRow;
    private final UUID craftingResultInventoryUuid;

    public CraftingTableEditEntity(UUID craftingInventoryUuid, int[][] inventory, int craftingNumberOfSlotParRow, int craftingNumberOfRow, UUID craftingResultInventoryUuid) {
        this.craftingInventoryUuid = craftingInventoryUuid;
        this.inventory = inventory;
        this.craftingNumberOfSlotParRow = craftingNumberOfSlotParRow;
        this.craftingNumberOfRow = craftingNumberOfRow;
        this.craftingResultInventoryUuid = craftingResultInventoryUuid;
    }

    public static CraftingTableEditEntity createCraftingTable(int craftingNumberOfSlotParRow, int craftingNumberOfRow) {
        return new CraftingTableEditEntity(UUID.randomUUID(), new int[craftingNumberOfSlotParRow * craftingNumberOfRow][InventoryComponent.DEFAULT_STACK_LIMITE], craftingNumberOfSlotParRow, craftingNumberOfRow, UUID.randomUUID());
    }

    public static CraftingTableEditEntity createSetCraftingTable(UUID craftingInventoryUuid, int[][] inventory, int craftingNumberOfSlotParRow, int craftingNumberOfRow, UUID craftingResultInventoryUuid) {
        return new CraftingTableEditEntity(craftingInventoryUuid, inventory, craftingNumberOfSlotParRow, craftingNumberOfRow, craftingResultInventoryUuid);
    }

    @Override
    public void editEntity(ExecuteOnContext executeOnContext, int entityId) {
        executeOnContext.onCommun(world -> world.getSystem(InventoryManager.class).createCraftingTable(entityId, craftingInventoryUuid, inventory, craftingNumberOfSlotParRow, craftingNumberOfRow, craftingResultInventoryUuid, RealmTechCoreMod.CRAFT));
    }
}
