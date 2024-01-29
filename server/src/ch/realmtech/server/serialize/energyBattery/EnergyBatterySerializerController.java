package ch.realmtech.server.serialize.energyBattery;

import ch.realmtech.server.energy.EnergyBatteryEditEntity;
import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;

public class EnergyBatterySerializerController extends AbstractSerializerController<Integer, EnergyBatteryEditEntity> {
    public EnergyBatterySerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 23, new HashMap<>() {
            {
                put((byte) 1, new EnergyBatterySerializerV1());
            }
        }, (byte) 1);
    }
}
