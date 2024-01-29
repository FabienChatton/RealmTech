package ch.realmtech.server.serialize.energyGenerator;

import ch.realmtech.server.energy.EnergyGeneratorEditEntity;
import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;

public class EnergyGeneratorSerializerController extends AbstractSerializerController<Integer, EnergyGeneratorEditEntity> {
    public EnergyGeneratorSerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 25, new HashMap<>() {
            {
                put((byte) 1, new EnergyGeneratorSerializerV1());
            }
        }, (byte) 1);
    }
}
