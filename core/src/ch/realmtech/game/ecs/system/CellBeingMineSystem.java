package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtechCommuns.ecs.component.CellBeingMineComponent;
import ch.realmtechCommuns.ecs.system.MapSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

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
