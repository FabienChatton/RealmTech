package ch.realmtech.server.energy;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.component.CellPaddingSerializableEditEntity;
import ch.realmtech.server.ecs.component.EnergyBatteryComponent;
import ch.realmtech.server.ecs.component.EnergyGeneratorComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.serialize.SerializerController;

import java.util.UUID;

public class EnergyGeneratorEditEntity implements EditEntity {

    private final UUID inventoryCarburantUuid;
    private final int[][] inventory;
    private final int numberOfRow;
    private final int numberOfSlotParRow;
    private final long stored;
    private final long capacity;

    public EnergyGeneratorEditEntity() {
        this(UUID.randomUUID(), new int[1][InventoryComponent.DEFAULT_STACK_LIMITE], 1, 1, 0, 10_000);
    }

    public EnergyGeneratorEditEntity(UUID inventoryCarburantUuid, int[][] inventory, int numberOfRow, int numberOfSlotParRow, long stored, long capacity) {
        this.inventoryCarburantUuid = inventoryCarburantUuid;
        this.inventory = inventory;
        this.numberOfRow = numberOfRow;
        this.numberOfSlotParRow = numberOfSlotParRow;
        this.stored = stored;
        this.capacity = capacity;
    }

    @Override
    public void editEntity(ExecuteOnContext executeOnContext, int entityId) {
        executeOnContext.onCommun((world) -> {
            SystemsAdminCommun systemsAdminCommun = world.getRegistered("systemsAdmin");
            systemsAdminCommun.inventoryManager.createInventoryUi(entityId, inventoryCarburantUuid, inventory, numberOfSlotParRow, numberOfRow);
            world.edit(entityId).create(EnergyGeneratorComponent.class);
            EnergyBatteryComponent energyBatteryComponent = world.edit(entityId).create(EnergyBatteryComponent.class).set(stored, capacity, EnergyBatteryComponent.EnergyBatteryRole.EMITTER_ONLY);
            world.edit(entityId).create(CellPaddingSerializableEditEntity.class).set(world.getRegistered(SerializerController.class).getEnergyGeneratorSerializerController());
        });
    }
}
