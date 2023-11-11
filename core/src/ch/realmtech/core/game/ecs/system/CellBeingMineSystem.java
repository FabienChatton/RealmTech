package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.plgin.SystemTickEmulation;
import ch.realmtech.core.game.ecs.plgin.SystemsAdminClient;
import ch.realmtech.core.input.InputMapper;
import ch.realmtech.server.ecs.component.CellBeingMineComponent;
import ch.realmtech.server.ecs.component.InfCellComponent;
import ch.realmtech.server.ecs.component.InfChunkComponent;
import ch.realmtech.server.ecs.component.InfMapComponent;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.mod.RealmTechCoreMod;
import ch.realmtech.server.packet.serverPacket.CellBreakRequestPacket;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

@SystemTickEmulation
@All({CellBeingMineComponent.class})
public class CellBeingMineSystem extends IteratingSystem {
    @Wire(name = "context")
    private RealmTech context;
    @Wire
    private SystemsAdminClient systemsAdminClient;
    private ComponentMapper<CellBeingMineComponent> mCellBeingMine;
    private ComponentMapper<InfCellComponent> mCell;
    private ComponentMapper<InfChunkComponent> mChunk;

    @Override
    protected void process(int entityId) {
        int[] infChunks = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class).infChunks;

        Vector2 screenCoordinate = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector2 gameCoordinate = context.getEcsEngine().getGameCoordinate(screenCoordinate);
        int worldPosX = MapManager.getWorldPos(gameCoordinate.x);
        int worldPosY = MapManager.getWorldPos(gameCoordinate.y);

        int chunk = systemsAdminClient.mapManager.getChunk(MapManager.getChunkPos(worldPosX), MapManager.getChunkPos(worldPosY), infChunks);
        int topCell = systemsAdminClient.mapManager.getTopCell(chunk, MapManager.getInnerChunk(worldPosX), MapManager.getInnerChunk(worldPosY));
        InfChunkComponent infChunkComponent = mChunk.get(chunk);
        InfCellComponent infCellComponent = mCell.get(topCell);

        if (topCell == entityId && InputMapper.leftClick.isPressed) {
            CellBeingMineComponent cellBeingMineComponent = mCellBeingMine.get(entityId);
            context.getSoundManager().playBreakingCell();
            if (cellBeingMineComponent.step == CellBeingMineComponent.INFINITE_MINE) return;
            if (cellBeingMineComponent.currentStep++ >= cellBeingMineComponent.step) {

                context.getConnexionHandler().sendAndFlushPacketToServer(new CellBreakRequestPacket(worldPosX, worldPosY, RealmTechCoreMod.NO_ITEM));
                mCellBeingMine.remove(topCell);
            }
        } else {
            mCellBeingMine.remove(entityId);
        }
    }
}
