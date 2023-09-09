package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;

public class PlayerInputSystem extends BaseSystem {
    @Wire(name = "context")
    private RealmTech context;

    @Override
    protected void processSystem() {
//        Vector3 gameCoordinate = context.getGameStage().getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
//        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
//            int chunk = world.getSystem(MapSystem.class).getChunk(context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class).infChunks, gameCoordinate.x, gameCoordinate.y);
//            byte innerChunkX = MapSystem.getInnerChunk(gameCoordinate.x);
//            byte innerChunkY = MapSystem.getInnerChunk(gameCoordinate.y);
//            int topCellId = context.getSystem(MapSystem.class).getTopCell(chunk, innerChunkX, innerChunkY);
//            world.getSystem(MapSystem.class).addCellBeingMine(topCellId);
//        } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
//            world.getSystem(MapSystem.class).interagieClickDroit(context.getEcsEngine().getPlayerId(), Input.Buttons.RIGHT, MapSystem.getChunkInUse(context), gameCoordinate.x, gameCoordinate.y, world.getSystem(ItemBarManager.class).getSelectItem());
//        }
    }
}
