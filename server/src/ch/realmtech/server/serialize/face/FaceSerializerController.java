package ch.realmtech.server.serialize.face;

import ch.realmtech.server.ecs.component.FaceComponent;
import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;

public class FaceSerializerController extends AbstractSerializerController<FaceComponent, Byte> {
    public FaceSerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 22, new HashMap<>() {
            {
                put((byte) 1, new FaceSerializerV1());
            }
        }, (byte) 1);
    }
}
