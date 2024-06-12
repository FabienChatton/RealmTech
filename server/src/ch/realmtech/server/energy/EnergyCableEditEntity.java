package ch.realmtech.server.energy;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.component.EnergyTransporterComponent;
import ch.realmtech.server.ecs.component.FaceComponent;
import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.serialize.SerializerController;

public class EnergyCableEditEntity implements EditEntity {
    private final byte face;

    public EnergyCableEditEntity(byte face) {
        this.face = face;
    }

    @Override
    public void createEntity(ExecuteOnContext executeOnContext, int entityId) {
        executeOnContext.onCommun((systemsAdminCommun, world) -> {
            FaceComponent faceComponent = world.edit(entityId).create(FaceComponent.class);
            faceComponent.set(face, true);
            faceComponent.setBaseTextures("energy-cable-01");
            world.edit(entityId).create(EnergyTransporterComponent.class).set();
            systemsAdminCommun.getCellPaddingManager().addOrCreate(entityId, world.getRegistered(SerializerController.class).getEnergyCableSerializerController());
        });
    }

    @Override
    public void deleteEntity(ExecuteOnContext executeOnContext, int entityId) {

    }

}
