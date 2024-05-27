package ch.realmtech.server.serialize.physicEntity;

import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;

public class EnemySerializerController extends AbstractSerializerController<Integer, PhysicEntityArgs> {

    public EnemySerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 19, new HashMap<>() {
            {
                put((byte) 1, new EnemySerializerV1());
            }
        }, (byte) 1);
    }
}
