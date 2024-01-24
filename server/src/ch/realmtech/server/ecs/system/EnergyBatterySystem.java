package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.CellComponent;
import ch.realmtech.server.ecs.component.EnergyBatteryComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.energy.EnergyTransportStatus;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

@All({EnergyBatteryComponent.class, CellComponent.class})
public class EnergyBatterySystem extends IteratingSystem {
    @Wire
    private SystemsAdminServer systemsAdminServer;
    private ComponentMapper<EnergyBatteryComponent> mEnergyBattery;
    @Override
    protected void process(int entityId) {
        EnergyBatteryComponent energyBatteryComponent = mEnergyBattery.get(entityId);
        if (energyBatteryComponent.isEnergyBatteryReceiver()) {
            EnergyTransportStatus energyTransportStatus = systemsAdminServer.energyManager.findEnergyToFeed(entityId);
            if (energyTransportStatus != null) {
                int batteryEmitterId = energyTransportStatus.batteryEmitterId();
                EnergyBatteryComponent energyBatteryComponentEmitter = mEnergyBattery.get(batteryEmitterId);
                energyBatteryComponentEmitter.removeStored(1);
                energyBatteryComponent.addStored(1);
            }
        }
    }
}
