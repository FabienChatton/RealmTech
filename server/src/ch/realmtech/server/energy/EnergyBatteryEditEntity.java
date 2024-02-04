package ch.realmtech.server.energy;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.component.EnergyBatteryComponent;
import ch.realmtech.server.ecs.component.FaceComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.serialize.SerializerController;

import java.util.UUID;

public class EnergyBatteryEditEntity implements EditEntity {

    private UUID energyBatteryUuid;
    private long stored;
    private long capacity;
    private byte face;

    public EnergyBatteryEditEntity() {

    }

    public static EnergyBatteryEditEntity create(UUID energyBatteryUuid, long stored, long capacity, byte face) {
        EnergyBatteryEditEntity energyBatteryEditEntity = new EnergyBatteryEditEntity();
        energyBatteryEditEntity.energyBatteryUuid = energyBatteryUuid;
        energyBatteryEditEntity.stored = stored;
        energyBatteryEditEntity.capacity = capacity;
        energyBatteryEditEntity.face = face;
        return energyBatteryEditEntity;
    }

    public static EnergyBatteryEditEntity createDefault() {
        return create(null,1_000, 10_000, FaceComponent.SOUTH);
    }

    @Override
    public void createEntity(ExecuteOnContext executeOnContext, int entityId) {
        executeOnContext.onCommun((world) -> {
            world.edit(entityId).create(EnergyBatteryComponent.class).set(stored, capacity, EnergyBatteryComponent.EnergyBatteryRole.EMITTER_RECEIVER);
            SystemsAdminCommun systemsAdminCommun = world.getRegistered("systemsAdmin");
            systemsAdminCommun.cellPaddingManager.addOrCreate(entityId, world.getRegistered(SerializerController.class).getEnergyBatterySerializerController());
            FaceComponent faceComponent = world.edit(entityId).create(FaceComponent.class);
            faceComponent.setMultiFace(false);
            faceComponent.setFace(face);
            faceComponent.setBaseTextures("energy-battery-01");
            systemsAdminCommun.uuidComponentManager.createRegisteredComponent(energyBatteryUuid != null ? energyBatteryUuid : UUID.randomUUID(), entityId);
        });
    }

    @Override
    public void deleteEntity(ExecuteOnContext executeOnContext, int entityId) {

    }

    @Override
    public void replaceEntity(ExecuteOnContext executeOnContext, int entityId) {

    }
}
