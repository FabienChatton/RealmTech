package ch.realmtech.server.serialize.physicEntity;

import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;

public class PhysicEntitySerializerController extends AbstractSerializerController<Integer, PhysicEntityArgs> {
    public final static byte PLAYER_FLAG = 1;
    public final static byte ENEMY_FLAG = 2;
    public final static byte ITEM_FLAG = 3;

    public PhysicEntitySerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 19, new HashMap<>() {
            {
                put((byte) 1, new PhysicEntitySerializerV1());
            }
        }, (byte) 1);
    }
}
