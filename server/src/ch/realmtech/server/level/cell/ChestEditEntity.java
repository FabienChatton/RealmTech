package ch.realmtech.server.level.cell;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.uuid.UuidSupplierOrRandom;
import com.badlogic.gdx.utils.Null;

import java.util.UUID;

public class ChestEditEntity implements EditEntity {
    private final UuidSupplierOrRandom uuid;
    @Null
    private final int[][] inventory;
    private final int numberOfSlotParRow;
    private final int numberOfRow;

    private ChestEditEntity(UuidSupplierOrRandom uuid, int[][] inventory, int numberOfSlotParRow, int numberOfRow) {
        this.uuid = uuid;
        this.inventory = inventory;
        this.numberOfSlotParRow = numberOfSlotParRow;
        this.numberOfRow = numberOfRow;
    }

    public static ChestEditEntity createNewInventory(int numberOfSlotParRow, int numberOfRow) {
        return new ChestEditEntity(new UuidSupplierOrRandom(), null, numberOfSlotParRow, numberOfRow);
    }

    public static ChestEditEntity createSetInventory(UUID uuid, int[][] inventory, int numberOfSlotParRow, int numberOfRow) {
        return new ChestEditEntity(new UuidSupplierOrRandom(uuid), inventory, numberOfSlotParRow, numberOfRow);
    }

    @Override
    public void createEntity(ExecuteOnContext executeOnContext, int motherEntityId) {
        executeOnContext.onCommun((world -> world.getSystem(InventoryManager.class).createChest(motherEntityId, inventory != null ? inventory : new int[numberOfSlotParRow * numberOfRow][InventoryComponent.DEFAULT_STACK_LIMITE], getUuid(), getNumberOfSlotParRow(), getNumberOfRow())));
    }

    @Override
    public void deleteEntity(ExecuteOnContext executeOnContext, int entityId) {
        // executeOnContext.onServer((world) -> world.getSystem(MapSystemServer.class).deleteChestDropItemServer(entityId));
        executeOnContext.onCommun((world) -> {
            InventoryComponent chestInventory = ((SystemsAdminCommun) world.getRegistered("systemsAdmin")).inventoryManager.getChestInventory(entityId);
            world.getSystem(InventoryManager.class).removeInventory(chestInventory.inventory);
        });
    }

    public UUID getUuid() {
        return uuid.get();
    }

    public int[][] getInventory() {
        return inventory;
    }

    public int getNumberOfSlotParRow() {
        return numberOfSlotParRow;
    }

    public int getNumberOfRow() {
        return numberOfRow;
    }
}
