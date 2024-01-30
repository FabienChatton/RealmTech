package ch.realmtech.server.energy;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.component.EnergyBatteryComponent;
import ch.realmtech.server.ecs.component.FaceComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.serialize.SerializerController;

public class EnergyBatteryEditEntity implements EditEntity {

    private final long stored;
    private final long capacity;
    private final byte face;
    public EnergyBatteryEditEntity(long stored, long capacity, byte face) {
        this.stored = stored;
        this.capacity = capacity;
        this.face = face;
    }

    @Override
    public void editEntity(ExecuteOnContext executeOnContext, int entityId) {
        executeOnContext.onCommun((world) -> {
            world.edit(entityId).create(EnergyBatteryComponent.class).set(stored, capacity, EnergyBatteryComponent.EnergyBatteryRole.EMITTER_RECEIVER);
            SystemsAdminCommun systemsAdminCommun = world.getRegistered("systemsAdmin");
            systemsAdminCommun.cellPaddingManager.addOrCreate(entityId, world.getRegistered(SerializerController.class).getEnergyBatterySerializerController());
            FaceComponent faceComponent = world.edit(entityId).create(FaceComponent.class);
            faceComponent.setMultiFace(false);
            faceComponent.setFace(face);
            faceComponent.setBaseTextures("energy-battery-01");
        });
    }
}
