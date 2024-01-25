package ch.realmtech.server.ecs.component;

import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.serialize.AbstractSerializerController;
import com.artemis.Component;

public class CellPaddingSerializableEditEntity extends Component {
    private AbstractSerializerController<Integer, ? extends EditEntity> serializerController;

    public CellPaddingSerializableEditEntity set(AbstractSerializerController<Integer, ? extends EditEntity> serializerController) {
        this.serializerController = serializerController;
        return this;
    }

    public AbstractSerializerController<Integer, ? extends EditEntity> getSerializerController() {
        return serializerController;
    }
}
