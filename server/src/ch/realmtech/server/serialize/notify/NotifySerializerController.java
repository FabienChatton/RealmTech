package ch.realmtech.server.serialize.notify;

import ch.realmtech.server.divers.Notify;
import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;

public class NotifySerializerController extends AbstractSerializerController<Notify, Notify> {
    public NotifySerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 27, new HashMap<>() {
            {
                put((byte) 1, new NotifySerializerV1());
            }
        }, (byte) 1);
    }
}
