package ch.realmtech.server.level.cell;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.ecs.system.MapSystemServer;
import com.badlogic.gdx.utils.Null;

import java.util.UUID;

public class ChestEditEntity implements EditEntity {
    private final UUID uuid;
    @Null
    private final int[][] inventory;
    private final int numberOfSlotParRow;
    private final int numberOfRow;

    private ChestEditEntity(UUID uuid, int[][] inventory, int numberOfSlotParRow, int numberOfRow) {
        this.uuid = uuid;
        this.inventory = inventory;
        this.numberOfSlotParRow = numberOfSlotParRow;
        this.numberOfRow = numberOfRow;
    }

    public static ChestEditEntity createNewInventory(int numberOfSlotParRow, int numberOfRow) {
        return new ChestEditEntity(UUID.randomUUID(), null, numberOfSlotParRow, numberOfRow);
    }

    public static ChestEditEntity createSetInventory(UUID uuid, int[][] inventory, int numberOfSlotParRow, int numberOfRow) {
        return new ChestEditEntity(uuid, inventory, numberOfSlotParRow, numberOfRow);
    }

    @Override
    public void createEntity(ExecuteOnContext executeOnContext, int motherEntityId) {
        executeOnContext.onCommun((world -> world.getSystem(InventoryManager.class).createChest(motherEntityId, inventory != null ? inventory : new int[numberOfSlotParRow * numberOfRow][InventoryComponent.DEFAULT_STACK_LIMITE], getUuid(), getNumberOfSlotParRow(), getNumberOfRow())));
    }

    @Override
    public void deleteEntity(ExecuteOnContext executeOnContext, int entityId) {
        executeOnContext.onServer((world) -> world.getSystem(MapSystemServer.class).deleteChestDropItemServer(entityId));
    }

    @Override
    public void replaceEntity(ExecuteOnContext executeOnContext, int entityId) {
        executeOnContext.onCommun((world) -> {
            int chestInventoryId = world.getSystem(InventoryManager.class).getChestInventoryId(entityId);
            InventoryComponent chestInventory = world.getSystem(InventoryManager.class).getChestInventory(entityId);
            world.getSystem(InventoryManager.class).removeInventory(chestInventory.inventory);
            world.delete(chestInventoryId);
        });
    }

    public UUID getUuid() {
        return uuid;
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
