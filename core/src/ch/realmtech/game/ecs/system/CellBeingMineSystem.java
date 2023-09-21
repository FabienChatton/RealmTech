package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtechServer.ecs.component.CellBeingMineComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

@All({CellBeingMineComponent.class})
public class CellBeingMineSystem extends IteratingSystem {
    @Wire(name = "context")
    private RealmTech context;
    private ComponentMapper<CellBeingMineComponent> mCellBeingMine;

    @Override
    protected void process(int entityId) {
//        int chunk = world.getSystem(MapSystem.class).findChunk(MapSystem.getChunkInUse(context), entityId);
//
//        // trouve la cellule qui devrait être miné pour être sûr qu'elle est toujours minée
//        Vector2 screenCoordinate = new Vector2(Gdx.input.getX(), Gdx.input.getY());
//        Vector3 gameCoordinate = MapSystem.getGameCoordinate(context, screenCoordinate);
//        int expectChunk = world.getSystem(MapSystem.class).getChunk(MapSystem.getChunkInUse(context), gameCoordinate.x, gameCoordinate.y);
//        int topCell = MapSystem.getTopCell(context, expectChunk, screenCoordinate);
//        if (topCell == entityId && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
//            CellBeingMineComponent cellBeingMineComponent = mCellBeingMine.get(entityId);
//            context.getSoundManager().playBreakingCell();
//            if (cellBeingMineComponent.step == CellBeingMineComponent.INFINITE_MINE) return;
//            if (cellBeingMineComponent.currentStep++ >= cellBeingMineComponent.step) {
//                world.getSystem(MapSystem.class).breakCell(chunk, entityId, context.getEcsEngine().getPlayerId());
//                remove(entityId);
//            }
//        } else {
//            remove(entityId);
//        }
    }

    private void remove(int entityId) {
        mCellBeingMine.remove(entityId);
    }
}
