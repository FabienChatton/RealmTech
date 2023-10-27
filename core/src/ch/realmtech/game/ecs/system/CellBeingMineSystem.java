package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.input.InputMapper;
import ch.realmtechServer.ecs.component.CellBeingMineComponent;
import ch.realmtechServer.ecs.component.InfCellComponent;
import ch.realmtechServer.ecs.component.InfChunkComponent;
import ch.realmtechServer.ecs.component.InfMapComponent;
import ch.realmtechServer.ecs.system.MapManager;
import ch.realmtechServer.mod.RealmTechCoreMod;
import ch.realmtechServer.packet.serverPacket.CellBreakRequestPacket;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

@All({CellBeingMineComponent.class})
public class CellBeingMineSystem extends IteratingSystem {
    @Wire(name = "context")
    private RealmTech context;
    private ComponentMapper<CellBeingMineComponent> mCellBeingMine;
    private ComponentMapper<InfCellComponent> mCell;
    private ComponentMapper<InfChunkComponent> mChunk;

    @Override
    protected void process(int entityId) {
        int[] infChunks = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class).infChunks;

        Vector2 screenCoordinate = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector2 gameCoordinate = context.getEcsEngine().getGameCoordinate(screenCoordinate);
        int expectChunk = world.getSystem(MapManager.class).getChunk(MapManager.getChunkPos(gameCoordinate.x), MapManager.getChunkPos(gameCoordinate.y), infChunks);
        int topCell = context.getSystem(MapManager.class).getTopCell(expectChunk, MapManager.getInnerChunk(gameCoordinate.x), MapManager.getInnerChunk(gameCoordinate.y));
        InfChunkComponent infChunkComponent = mChunk.get(expectChunk);
        InfCellComponent infCellComponent = mCell.get(topCell);

        if (topCell == entityId && InputMapper.leftClick.isPressed) {
            CellBeingMineComponent cellBeingMineComponent = mCellBeingMine.get(entityId);
            context.getSoundManager().playBreakingCell();
            if (cellBeingMineComponent.step == CellBeingMineComponent.INFINITE_MINE) return;
            if (cellBeingMineComponent.currentStep++ >= cellBeingMineComponent.step) {
                int worldPosX = MapManager.getWorldPos(gameCoordinate.x);
                int worldPosY = MapManager.getWorldPos(gameCoordinate.y);
                context.getConnexionHandler().sendAndFlushPacketToServer(new CellBreakRequestPacket(worldPosX, worldPosY, RealmTechCoreMod.NO_ITEM));
                mCellBeingMine.remove(topCell);
            }
        } else {
            mCellBeingMine.remove(entityId);
        }
    }
}
