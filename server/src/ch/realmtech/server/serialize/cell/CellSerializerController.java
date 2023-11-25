package ch.realmtech.server.serialize.cell;

import ch.realmtech.server.ecs.component.InfCellComponent;
import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;

public class CellSerializerController extends AbstractSerializerController<InfCellComponent, CellArgs> {
    public CellSerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 15, new HashMap<>() {
            {
                put((byte) 1, new CellSerializerV1());
            }
        }, (byte) 1);
    }
}
