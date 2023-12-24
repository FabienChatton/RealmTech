package ch.realmtech.server.serialize.life;

import ch.realmtech.server.ecs.component.LifeComponent;
import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;

public class LifeSerializerController extends AbstractSerializerController<LifeComponent, LifeArgs> {
    public LifeSerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 20, new HashMap<>() {
            {
                {
                    put((byte) 1, new LifeSerializerV1());
                }
            }
        }, (byte) 1);
    }
}
