package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.energy.EnergyTransportStatus;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

public class EnergyManager extends Manager {
    @Wire(name = "systemsAdmin")
    private SystemsAdminCommun systemsAdminCommun;
    private ComponentMapper<CellComponent> mCell;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<EnergyBatteryComponent> mEnergyBattery;
    private ComponentMapper<EnergyTransporterComponent> mEnergyTransporter;
    private ComponentMapper<FaceComponent> mFace;
    private final static byte[][] FIND_ENERGY_PROVIDER_POSS = {{0, 1}, {-1, 0}, {1, 0}, {0, -1}};

    public static boolean isEnergyBatteryEmitter(EnergyBatteryComponent energyBatteryComponent) {
        return (energyBatteryComponent.getEnergyBatteryRole() & EnergyBatteryComponent.EnergyBatteryRole.EMITTER_ONLY.getRoleByte()) > 0 &&
                energyBatteryComponent.getStored() > 0;
    }

    public static boolean isEnergyBatteryReceiver(EnergyBatteryComponent energyBatteryComponent) {
        return (energyBatteryComponent.getEnergyBatteryRole() & EnergyBatteryComponent.EnergyBatteryRole.RECEIVER_ONLY.getRoleByte()) > 0 &&
                !isFull(energyBatteryComponent);
    }

    public static boolean isFull(EnergyBatteryComponent energyBatteryComponent) {
        return energyBatteryComponent.getStored() >= energyBatteryComponent.getCapacity();
    }

    public EnergyTransportStatus findEnergyToFeed(int energyReceiverId) {
        CellComponent energyReceiverCellComponent = mCell.get(energyReceiverId);
        InfChunkComponent energyReceiverChunkComponent = mChunk.get(energyReceiverCellComponent.chunkId);

        int worldPosX = MapManager.getWorldPos(energyReceiverChunkComponent.chunkPosX, energyReceiverCellComponent.getInnerPosX());
        int worldPosY = MapManager.getWorldPos(energyReceiverChunkComponent.chunkPosY, energyReceiverCellComponent.getInnerPosY());

        FaceComponent faceComponent = mFace.get(energyReceiverId);
        int cellEnergyProvider = findCellEnergyProvider(
                worldPosX,
                worldPosY,
                energyReceiverCellComponent.cellRegisterEntry.getCellBehavior().getLayer(),
                faceComponent != null ? faceComponent.getFaceInverted() : FaceComponent.ALL_FACE // energy input face allow
        );
        if (cellEnergyProvider != -1) {
            return new EnergyTransportStatus(energyReceiverId, cellEnergyProvider);
        } else {
            return null;
        }
    }

    private int findCellEnergyProvider(int worldPosX, int worldPosY, byte layer, byte allowFace) {
        Queue<FindCellProviderArg> cellQueue = new ArrayDeque<>();
        Stack<int[]> initialVisitedCell = new Stack<>();
        initialVisitedCell.add(new int[]{worldPosX, worldPosY});
        cellQueue.add(new FindCellProviderArg(worldPosX, worldPosY, allowFace, initialVisitedCell));

        while (!cellQueue.isEmpty()) {
            FindCellProviderArg findCellProviderArg = cellQueue.poll();
            int[] preWorldPos = findCellProviderArg.visitedCell.peek();
            int preWorldPosX = preWorldPos[0];
            int preWorldPosY = preWorldPos[1];

            for (int i = 0; i < FIND_ENERGY_PROVIDER_POSS.length; i++) {
                int worldPosXFind = findCellProviderArg.worldPosX + FIND_ENERGY_PROVIDER_POSS[i][0];
                int worldPosYFind = findCellProviderArg.worldPosY + FIND_ENERGY_PROVIDER_POSS[i][1];

                if (worldPosX == worldPosXFind && worldPosY == worldPosYFind) {
                    continue;
                }

                if (findCellProviderArg.visitedCell.stream().anyMatch((visitedCell) -> visitedCell[0] == worldPosXFind && visitedCell[1] == worldPosYFind)) {
                    continue;
                }

                byte faceNext = FaceComponent.getFace(preWorldPosX, preWorldPosY, worldPosXFind, worldPosYFind);
                byte faceFrom = FaceComponent.getFace(worldPosXFind, worldPosYFind, preWorldPosX, preWorldPosY);

                if ((faceNext & findCellProviderArg.allowFace) == 0) {
                    continue;
                }

                int[] infChunks = systemsAdminCommun.mapManager.getInfMap().infChunks;
                int chunkId = systemsAdminCommun.mapManager.getChunkByWorldPos(worldPosXFind, worldPosYFind, infChunks);
                if (chunkId == -1) {
                    continue;
                }

                int nextCellId = systemsAdminCommun.mapManager.getCell(chunkId, worldPosXFind, worldPosYFind, layer);
                if (nextCellId == -1) {
                    continue;
                }

                if (isCellEnergyEmitter(nextCellId)) {
                    FaceComponent faceEmitterComponent = mFace.get(nextCellId);
                    byte faceEmitterByte = faceEmitterComponent != null ? faceEmitterComponent.getFace() : FaceComponent.ALL_FACE;
                    if ((faceFrom & faceEmitterByte) != 0) {
                        return nextCellId;
                    }
                }

                if (isCellEnergyTransporter(nextCellId)) {
                    FaceComponent nextFaceComponent = mFace.get(nextCellId);
                    if ((faceFrom & nextFaceComponent.getFace()) != 0) {
                        Stack<int[]> visitedCell = new Stack<>();
                        visitedCell.addAll(findCellProviderArg.visitedCell);
                        visitedCell.add(new int[]{worldPosXFind, worldPosYFind});
                        cellQueue.add(new FindCellProviderArg(worldPosXFind, worldPosYFind, nextFaceComponent.getFace(), visitedCell));
                    }
                }
            }
        }
        return -1;
    }

    private record FindCellProviderArg(int worldPosX, int worldPosY, byte allowFace, Stack<int[]> visitedCell) {
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
