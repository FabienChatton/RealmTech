package ch.realmtech.server.ecs.component;

import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.serialize.AbstractSerializerController;
import com.artemis.Component;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;

public class CellPaddingSerializableEditEntity extends Component {
    private Bag<AbstractSerializerController<Integer, ? extends EditEntity>> serializerControllers;

    public CellPaddingSerializableEditEntity set() {
        this.serializerControllers = new Bag<>();
        return this;
    }

    public void add(AbstractSerializerController<Integer, ? extends EditEntity> serializerController) {
        serializerControllers.add(serializerController);
    }

    public ImmutableBag<AbstractSerializerController<Integer, ? extends EditEntity>> getSerializerControllers() {
        return serializerControllers;
    }
}
