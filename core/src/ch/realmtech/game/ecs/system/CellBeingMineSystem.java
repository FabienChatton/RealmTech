package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtechServer.ecs.component.CellBeingMineComponent;
import ch.realmtechServer.ecs.component.InfMapComponent;
import ch.realmtechServer.ecs.system.MapManager;
import ch.realmtechServer.mod.RealmTechCoreMod;
import ch.realmtechServer.packet.serverPacket.CellBreakRequestPacket;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

@All({CellBeingMineComponent.class})
public class CellBeingMineSystem extends IteratingSystem {
    @Wire(name = "context")
    private RealmTech context;
    private ComponentMapper<CellBeingMineComponent> mCellBeingMine;

    @Override
    protected void process(int entityId) {
        int[] infChunks = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class).infChunks;
        int chunk = world.getSystem(MapManager.class).findChunk(infChunks, entityId);

        // trouve la cellule qui devrait être miné pour être sûr qu'elle est toujours minée
        Vector2 screenCoordinate = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector2 gameCoordinate = context.getEcsEngine().getGameCoordinate(screenCoordinate);
        int expectChunk = world.getSystem(MapManager.class).getChunk(infChunks, gameCoordinate.x, gameCoordinate.y);
        int topCell = context.getEcsEngine().getTopCell(expectChunk, screenCoordinate);
        if (topCell == entityId && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            CellBeingMineComponent cellBeingMineComponent = mCellBeingMine.get(entityId);
            context.getSoundManager().playBreakingCell();
            if (cellBeingMineComponent.step == CellBeingMineComponent.INFINITE_MINE) return;
            if (cellBeingMineComponent.currentStep++ >= cellBeingMineComponent.step) {
                int chunkPosX = MapManager.getChunkPos(gameCoordinate.x);
                int chunkPosY = MapManager.getChunkPos(gameCoordinate.y);
                byte innerChunkX = MapManager.getInnerChunk(gameCoordinate.x);
                byte innerChunkY = MapManager.getInnerChunk(gameCoordinate.y);
                context.getConnexionHandler().sendAndFlushPacketToServer(new CellBreakRequestPacket(chunkPosX, chunkPosY, innerChunkX, innerChunkY, RealmTechCoreMod.NO_ITEM));
                mCellBeingMine.remove(topCell);
            }
        } else {
            mCellBeingMine.remove(entityId);
        }
    }
}
