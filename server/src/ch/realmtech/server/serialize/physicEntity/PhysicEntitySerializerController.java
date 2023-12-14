package ch.realmtech.server.serialize.physicEntity;

import ch.realmtech.server.packet.clientPacket.ClientExecute;
import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;
import java.util.function.Consumer;

public class PhysicEntitySerializerController extends AbstractSerializerController<Integer, Consumer<ClientExecute>> {
    final static byte PLAYER_FLAG = 1;
    final static byte ENEMY_FLAG = 2;

    public PhysicEntitySerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 19, new HashMap<>() {
            {
                put((byte) 1, new PhysicEntitySerializerV1());
            }
        }, (byte) 1);
    }
}
