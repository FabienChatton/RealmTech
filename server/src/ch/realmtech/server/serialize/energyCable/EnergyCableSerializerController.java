package ch.realmtech.server.serialize.energyCable;

import ch.realmtech.server.energy.EnergyCableEditEntity;
import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;

public class EnergyCableSerializerController extends AbstractSerializerController<Integer, EnergyCableEditEntity> {
    public EnergyCableSerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 24, new HashMap<>() {
            {
                put((byte) 1, new EnergyCableSerializerV1());
            }
        }, (byte) 1);
    }
}
