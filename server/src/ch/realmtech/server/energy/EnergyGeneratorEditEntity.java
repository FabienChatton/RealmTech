package ch.realmtech.server.energy;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.component.EnergyBatteryComponent;
import ch.realmtech.server.ecs.component.EnergyGeneratorComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.uuid.UuidSupplierOrRandom;

public class EnergyGeneratorEditEntity implements EditEntity {
    private final UuidSupplierOrRandom energyGeneratorUuid;
    private final int remainingTickToBurn;
    private final long stored;
    private final long capacity;

    private EnergyGeneratorEditEntity() {
        this(new UuidSupplierOrRandom(), 0, 0, 10_000);
    }

    public static EnergyGeneratorEditEntity createDefault() {
        return new EnergyGeneratorEditEntity();
    }

    public EnergyGeneratorEditEntity(UuidSupplierOrRandom energyGeneratorUuid, int remainingTickToBurn, long stored, long capacity) {
        this.energyGeneratorUuid = energyGeneratorUuid;
        this.remainingTickToBurn = remainingTickToBurn;
        this.stored = stored;
        this.capacity = capacity;
    }

    @Override
    public void createEntity(ExecuteOnContext executeOnContext, int entityId) {
        executeOnContext.onCommun((world) -> {
            SystemsAdminCommun systemsAdminCommun = world.getRegistered("systemsAdmin");

            systemsAdminCommun.uuidEntityManager.registerEntityIdWithUuid(energyGeneratorUuid.get(), entityId);
            world.edit(entityId).create(EnergyGeneratorComponent.class).set(remainingTickToBurn);
            world.edit(entityId).create(EnergyBatteryComponent.class).set(stored, capacity, EnergyBatteryComponent.EnergyBatteryRole.EMITTER_ONLY);
            systemsAdminCommun.cellPaddingManager.addOrCreate(entityId, world.getRegistered(SerializerController.class).getEnergyGeneratorSerializerController());
        });
        executeOnContext.onClient((systemsAdminClientForClient, world) -> {
            systemsAdminClientForClient.getEnergyBatteryIconSystem().createEnergyGeneratorIcon(entityId, new UuidSupplierOrRandom().get());
        });
    }

    @Override
    public void deleteEntity(ExecuteOnContext executeOnContext, int entityId) {
        executeOnContext.onClient((systemsAdminClientForClient, world) -> {
            systemsAdminClientForClient.getEnergyBatteryIconSystem().deleteGeneratorBatteryIcons(entityId);
        });
    }
}
