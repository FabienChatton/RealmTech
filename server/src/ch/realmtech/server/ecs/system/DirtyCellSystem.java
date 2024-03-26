package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.CellComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.packet.clientPacket.CellSetPacket;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.IntSet;

public class DirtyCellSystem extends BaseSystem {

    @Wire(name = "serverContext")
    private ServerContext serverContext;

    @Wire
    private SystemsAdminServer systemsAdminServer;
    private IntSet dirtyCellSet;

    private ComponentMapper<CellComponent> mCell;

    @Override
    protected void initialize() {
        dirtyCellSet = new IntSet();
    }

    @Override
    protected void processSystem() {
        IntSet.IntSetIterator iterator = dirtyCellSet.iterator();
        while (iterator.hasNext) {
            int cellId = iterator.next();
            CellComponent cellComponent = mCell.get(cellId);
            int worldPosX = systemsAdminServer.getMapManager().getWorldPosX(cellComponent);
            int worldPosY = systemsAdminServer.getMapManager().getWorldPosY(cellComponent);
            SerializedApplicationBytes cellEncode = serverContext.getSerializerController().getCellSerializerController().encode(cellId);
            serverContext.getServerConnexion().sendPacketToSubscriberForChunkPos(new CellSetPacket(worldPosX, worldPosY, cellComponent.cellRegisterEntry.getCellBehavior().getLayer(), cellEncode), MapManager.getChunkPos(worldPosX), MapManager.getChunkPos(worldPosY));
        }
    }

    public void addDirtyCell(int cellId) {
        dirtyCellSet.add(cellId);
    }

    @Override
    protected void end() {
        dirtyCellSet.clear();
    }
}
