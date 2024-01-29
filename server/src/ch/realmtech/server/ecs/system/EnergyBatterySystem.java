package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.CellComponent;
import ch.realmtech.server.ecs.component.EnergyBatteryComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.energy.EnergyTransportStatus;
import ch.realmtech.server.packet.clientPacket.CellSetPacket;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

@All({EnergyBatteryComponent.class, CellComponent.class})
public class EnergyBatterySystem extends IteratingSystem {
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Wire
    private SystemsAdminServer systemsAdminServer;
    private ComponentMapper<EnergyBatteryComponent> mEnergyBattery;
    private ComponentMapper<CellComponent> mCell;

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

                CellComponent cellComponent = mCell.get(entityId);
                int worldPosX = systemsAdminServer.mapManager.getWorldPosX(cellComponent);
                int worldPosY = systemsAdminServer.mapManager.getWorldPosY(cellComponent);
                serverContext.getServerHandler().broadCastPacket(new CellSetPacket(worldPosX, worldPosY, cellComponent.cellRegisterEntry.getCellBehavior().getLayer(), serverContext.getSerializerController().getCellSerializerController().encode(entityId)));
            }
        }
    }
}
