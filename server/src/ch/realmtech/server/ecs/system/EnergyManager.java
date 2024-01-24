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
    private ComponentMapper<EnergyBatteryComponent> mEnergyBattery;
    private ComponentMapper<EnergyTransporterComponent> mEnergyTransporter;
    private ComponentMapper<FaceComponent> mFace;
    private final static byte[][] findEnergyProviderPoss = {{0, 1}, {-1,0}, {1,0}, {0,-1}};

    public static boolean isEnergyBatteryEmitter(EnergyBatteryComponent energyBatteryComponent) {
        return energyBatteryComponent.getStored() > 0;
    }

    public static boolean isEnergyBatteryReceiver(EnergyBatteryComponent energyBatteryComponent) {
        return energyBatteryComponent.getStored() < energyBatteryComponent.getCapacity();
    }

    public EnergyTransportStatus findEnergyToFeed(int energyReceiverId) {
        CellComponent energyReceiverCellComponent = mCell.get(energyReceiverId);
        InfChunkComponent energyReceiverChunkComponent = mChunk.get(energyReceiverCellComponent.chunkId);

        int worldPosX = MapManager.getWorldPos(energyReceiverChunkComponent.chunkPosX, energyReceiverCellComponent.getInnerPosX());
        int worldPosY = MapManager.getWorldPos(energyReceiverChunkComponent.chunkPosY, energyReceiverCellComponent.getInnerPosY());

        int cellEnergyProvider = findCellEnergyProvider(
                worldPosX,
                worldPosY,
                worldPosX,
                worldPosY,
                energyReceiverCellComponent.cellRegisterEntry.getCellBehavior().getLayer(),
                mFace.get(energyReceiverId).getFaceInverted()
        );

        if (cellEnergyProvider != -1) {
            return new EnergyTransportStatus(energyReceiverId, cellEnergyProvider);
        } else {
            return null;
        }
    }

    private int findCellEnergyProvider(int worldPosX, int worldPosY, int preWorldPosX, int preWorldPosY, byte layer, byte allowFace) {
        for (int i = 0; i < findEnergyProviderPoss.length; i++) {
            int worldPosXFind = worldPosX + findEnergyProviderPoss[i][0];
            int worldPosYFind = worldPosY + findEnergyProviderPoss[i][1];

            if (worldPosXFind == preWorldPosX && worldPosYFind == preWorldPosY) {
                continue;
            }

            byte face = FaceComponent.getFace(worldPosX, worldPosY, worldPosXFind, worldPosYFind);

            if ((face & allowFace) == 0) {
                continue;
            }

            int[] infChunks = serverContext.getEcsEngineServer().getMapEntity().getComponent(InfMapComponent.class).infChunks;
            int chunkId = systemsAdminServer.mapManager.getChunkByWorldPos(worldPosXFind, worldPosYFind, infChunks);
            if (chunkId == -1) {
                continue;
            }

            int cellId = systemsAdminServer.mapManager.getCell(chunkId, worldPosXFind, worldPosYFind, layer);
            if (cellId == -1) {
                continue;
            }

            if (isCellEnergyEmitter(cellId)) {
                EnergyBatteryComponent energyBatteryComponent = mEnergyBattery.get(cellId);
                FaceComponent faceEmitterComponent = mFace.get(cellId);
                if ((face & faceEmitterComponent.getFaceInverted()) != 0) {
                    return cellId;
                }
            }

            if (isCellEnergyTransporter(cellId)) {
                int cellFind = findCellEnergyProvider(worldPosXFind, worldPosYFind, worldPosX, worldPosY, layer, mFace.get(cellId).getFlags());
                if (cellFind != -1) {
                    return cellFind;
                }
            }
        }
        return -1;
    }

    private boolean isCellEnergyEmitter(int cellId) {
        if (mEnergyBattery.has(cellId)) {
            return mEnergyBattery.get(cellId).isEnergyBatteryEmitter();
        }

        return false;
    }

    private boolean isCellEnergyTransporter(int cellId) {
        return mEnergyTransporter.has(cellId);
    }
}
