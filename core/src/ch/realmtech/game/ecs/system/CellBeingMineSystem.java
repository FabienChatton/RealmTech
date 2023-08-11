package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.CellBeingMineComponent;
import ch.realmtech.game.ecs.component.InfCellComponent;
import ch.realmtech.game.ecs.component.InfMapComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

@All({CellBeingMineComponent.class, InfCellComponent.class})
public class CellBeingMineSystem extends IteratingSystem {
    private final static float TIME_LAPS = 1 / 20f;
    private long lastTime;
    private long time;
    @Wire(name = "context")
    RealmTech context;

    ComponentMapper<CellBeingMineComponent> mCellBeingMine;
    ComponentMapper<InfCellComponent> mCell;

    @Override
    protected void process(int entityId) {
        time = System.currentTimeMillis();
        if (lastTime + time < TIME_LAPS + System.currentTimeMillis()) {
            return;
        }
        lastTime = time;
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            remove(entityId);
            return;
        }
        CellBeingMineComponent cellBeingMineComponent = mCellBeingMine.get(entityId);
        InfCellComponent infCellComponent = mCell.get(entityId);
        int[] infChunks = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class).infChunks;

        Vector2 screenCoordinate = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector3 gameCoordinate = context.getGameStage().getCamera().unproject(new Vector3(screenCoordinate, 0));
        int chunk = world.getSystem(MapSystem.class).getChunk(infChunks, gameCoordinate.x, gameCoordinate.y);
        int cell = world.getSystem(MapSystem.class).getCell(chunk, infCellComponent.innerPosX, infCellComponent.innerPosY, infCellComponent.cellRegisterEntry.getCellBehavior().getLayer());
        if (cell != -1) {
            if (cellBeingMineComponent.currentStep++ >= cellBeingMineComponent.step) {
                context.getSystem(MapSystem.class).breakTopCell(context.getEcsEngine().getPlayerId(), Input.Buttons.LEFT, context.getEcsEngine().getWorld().getMapper(InfMapComponent.class).get(context.getEcsEngine().getMapId()).infChunks, gameCoordinate.x, gameCoordinate.y);
                remove(entityId);
            }
        } else {
            remove(entityId);
        }
    }

    private void remove(int entityId) {
        mCellBeingMine.remove(entityId);
    }
}
