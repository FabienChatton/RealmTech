package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.CellComponent;
import ch.realmtech.server.ecs.component.EnergyBattery;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.energy.EnergyTransportStatus;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

@All({EnergyBattery.class, CellComponent.class})
public class EnergyBatterySystem extends IteratingSystem {
    @Wire
    private SystemsAdminServer systemsAdminServer;
    private ComponentMapper<EnergyBattery> mEnergyBattery;
    @Override
    protected void process(int entityId) {
        EnergyBattery energyBattery = mEnergyBattery.get(entityId);
        if (energyBattery.isEnergyBatteryReceiver()) {
            EnergyTransportStatus energyTransportStatus = systemsAdminServer.energyManager.findEnergyToFeed(entityId);
            if (energyTransportStatus != null) {
                int batteryEmitterId = energyTransportStatus.batteryEmitterId();
                EnergyBattery energyBatteryEmitter = mEnergyBattery.get(batteryEmitterId);
                energyBatteryEmitter.removeStored(1);
                energyBattery.addStored(1);
            }
        }
    }
}
