package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.CellPaddingSerializableEditEntity;
import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.serialize.AbstractSerializerController;
import com.artemis.ComponentMapper;
import com.artemis.Manager;

public class CellPaddingManager extends Manager {
    private ComponentMapper<CellPaddingSerializableEditEntity> mCellPadding;

    public void addOrCreate(int entityId, AbstractSerializerController<Integer, ? extends EditEntity> serializerController) {
        if (mCellPadding.has(entityId)) {
            mCellPadding.get(entityId).add(serializerController);
        } else {
            mCellPadding.create(entityId).set().add(serializerController);
        }
    }
}
