package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.core.input.InputMapper;
import ch.realmtech.server.ecs.component.CellBeingMineComponent;
import ch.realmtech.server.ecs.component.CellComponent;
import ch.realmtech.server.ecs.component.InfChunkComponent;
import ch.realmtech.server.ecs.component.InfMapComponent;
import ch.realmtech.server.ecs.plugin.SystemServerTickSlave;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.packet.serverPacket.CellBreakRequestPacket;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.util.UUID;

@SystemServerTickSlave
@All({CellBeingMineComponent.class})
public class CellBeingMineSystem extends IteratingSystem {
    @Wire(name = "context")
    private RealmTech context;
    @Wire
    private SystemsAdminClient systemsAdminClient;
    private ComponentMapper<CellBeingMineComponent> mCellBeingMine;
    private ComponentMapper<CellComponent> mCell;
    private ComponentMapper<InfChunkComponent> mChunk;

    @Override
    protected void process(int entityId) {
        int[] infChunks = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class).infChunks;

        Vector2 screenCoordinate = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector2 gameCoordinate = context.getEcsEngine().getGameCoordinate(screenCoordinate);
        int worldPosX = MapManager.getWorldPos(gameCoordinate.x);
        int worldPosY = MapManager.getWorldPos(gameCoordinate.y);

        int chunk = systemsAdminClient.getMapManager().getChunk(MapManager.getChunkPos(worldPosX), MapManager.getChunkPos(worldPosY), infChunks);
        int topCell = systemsAdminClient.getMapManager().getTopCell(chunk, MapManager.getInnerChunk(worldPosX), MapManager.getInnerChunk(worldPosY));
        InfChunkComponent infChunkComponent = mChunk.get(chunk);
        CellComponent cellComponent = mCell.get(topCell);

        if (topCell == entityId && InputMapper.leftClick.isPressed) {
            CellBeingMineComponent cellBeingMineComponent = mCellBeingMine.get(entityId);
            context.getSoundManager().playBreakingCell();
            if (cellBeingMineComponent.step == CellBeingMineComponent.INFINITE_MINE) return;
            if (cellBeingMineComponent.currentStep++ >= cellBeingMineComponent.step) {
                UUID itemUsedUuid;
                int selectItem = systemsAdminClient.getItemBarManager().getSelectItemOrNoting();
                if (selectItem != -1) {
                    itemUsedUuid = systemsAdminClient.getUuidEntityManager().getEntityUuid(selectItem);
                } else {
                    itemUsedUuid = null;
                }
                context.getClientConnexion().sendAndFlushPacketToServer(new CellBreakRequestPacket(worldPosX, worldPosY, itemUsedUuid));
                context.getSoundManager().playCellBreak();
                mCellBeingMine.remove(topCell);
            }
        } else {
            mCellBeingMine.remove(entityId);
        }
    }
}
