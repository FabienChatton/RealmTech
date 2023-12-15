package ch.realmtech.server.level.cell;

import ch.realmtech.server.ecs.system.InventoryManager;
import com.artemis.World;
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
    public void editEntity(World world, int entityId) {
        world.getSystem(InventoryManager.class).createChest(entityId, getUuid(), getNumberOfSlotParRow(), getNumberOfRow());
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
