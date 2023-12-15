package ch.realmtech.server.serialize.chest;

import ch.realmtech.server.level.cell.ChestEditEntity;
import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;

public class ChestSerializerController extends AbstractSerializerController<Integer, ChestEditEntity> {

    public ChestSerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 11, new HashMap<>() {
            {
                put((byte) 1, new ChestSerializerV1());
            }
        }, (byte) 1);
    }
}
