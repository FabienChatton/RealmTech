package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.energy.EnergyTransportStatus;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;

public class EnergyManager extends Manager {

    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Wire
    private SystemsAdminServer systemsAdminServer;
    private ComponentMapper<CellComponent> mCell;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<EnergyBattery> mEnergyBattery;
    private ComponentMapper<EnergyTransporter> mEnergyTransporter;
    public static boolean isEnergyBatteryEmitter(EnergyBattery energyBattery) {
        return energyBattery.getCapacity() > 0;
    }

    public static boolean isEnergyBatteryReceiver(EnergyBattery energyBattery) {
        return energyBattery.getStored() < energyBattery.getCapacity();
    }

    public EnergyTransportStatus findEnergyToFeed(int energyReceiverId) {
        CellComponent energyReceiverCellComponent = mCell.get(energyReceiverId);
        InfChunkComponent energyReceiverChunkComponent = mChunk.get(energyReceiverCellComponent.chunkId);

        int worldPosX = MapManager.getWorldPos(energyReceiverChunkComponent.chunkPosX, energyReceiverCellComponent.getInnerPosX());
        int worldPosY = MapManager.getWorldPos(energyReceiverChunkComponent.chunkPosY, energyReceiverCellComponent.getInnerPosY());

        int cellEnergyProvider = findCellEnergyProvider(worldPosX, worldPosY, energyReceiverCellComponent.cellRegisterEntry.getCellBehavior().getLayer());

        if (cellEnergyProvider != -1) {
            return new EnergyTransportStatus(energyReceiverId, cellEnergyProvider);
        } else {
            return null;
        }
    }

    private int findCellEnergyProvider(int worldPosX, int worldPosY, byte layer) {
        for (int x = -1; x < 1; x++) {
            for (int y = -1; y < 1; y++) {
                int worldPosXFind = worldPosX + x;
                int worldPosYFind = worldPosY + y;
                if (isCellEnergyProvider(worldPosXFind, worldPosYFind, layer)) {
                    int[] infChunks = serverContext.getEcsEngineServer().getMapEntity().getComponent(InfMapComponent.class).infChunks;
                    int chunkId = systemsAdminServer.mapManager.getChunkByWorldPos(worldPosX, worldPosY, infChunks);
                    if (chunkId == -1) {
                        continue;
                    }
                    return systemsAdminServer.mapManager.getCell(chunkId, worldPosXFind, worldPosYFind, layer);
                }

                if (isCellEnergyTransporter(worldPosXFind, worldPosYFind, layer)) {
                    int cellFind = findCellEnergyProvider(worldPosXFind, worldPosYFind, layer);
                    if (cellFind != -1) {
                        return cellFind;
                    }
                }
            }
        }
        return -1;
    }

    private boolean isCellEnergyProvider(int worldPosX, int worldPosY, byte layer) {
        int[] infChunks = serverContext.getEcsEngineServer().getMapEntity().getComponent(InfMapComponent.class).infChunks;
        int chunkId = systemsAdminServer.mapManager.getChunkByWorldPos(worldPosX, worldPosY, infChunks);
        if (chunkId == -1) {
            return false;
        }

        int cellId = systemsAdminServer.mapManager.getCell(chunkId, worldPosX, worldPosY, layer);
        if (cellId == -1) {
            return false;
        }

        if (mEnergyBattery.has(cellId)) {
            return mEnergyBattery.get(cellId).isEnergyBatteryEmitter();
        }

        return false;
    }

    private boolean isCellEnergyTransporter(int worldPosX, int worldPosY, byte layer) {
        int[] infChunks = serverContext.getEcsEngineServer().getMapEntity().getComponent(InfMapComponent.class).infChunks;
        int chunkId = systemsAdminServer.mapManager.getChunkByWorldPos(worldPosX, worldPosY, infChunks);
        if (chunkId == -1) {
            return false;
        }

        int cellId = systemsAdminServer.mapManager.getCell(chunkId, worldPosX, worldPosY, layer);
        if (cellId == -1) {
            return false;
        }

        return mEnergyTransporter.has(cellId);
    }
}
