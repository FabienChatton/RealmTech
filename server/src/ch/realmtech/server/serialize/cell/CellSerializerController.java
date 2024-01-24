package ch.realmtech.server.serialize.cell;

import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;

public class CellSerializerController extends AbstractSerializerController<Integer, CellArgs> {
    public CellSerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 15, new HashMap<>() {
            {
                put((byte) 1, new CellSerializerV1());
                put((byte) 2, new CellSerializerV2());
                put((byte) 3, new CellSerializerV3());
            }
        }, (byte) 3);
    }
}
