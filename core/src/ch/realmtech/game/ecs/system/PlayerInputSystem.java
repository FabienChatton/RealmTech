package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtechServer.ecs.component.InfCellComponent;
import ch.realmtechServer.ecs.component.InfMapComponent;
import ch.realmtechServer.ecs.system.InventoryManager;
import ch.realmtechServer.ecs.system.MapManager;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;

public class PlayerInputSystem extends BaseSystem {
    @Wire(name = "context")
    private RealmTech context;
    private ComponentMapper<InfCellComponent> mCell;

    @Override
    protected void processSystem() {
        Vector3 gameCoordinate = context.getGameStage().getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        MapManager mapManger = world.getSystem(MapManager.class);
        InfMapComponent infMapComponent = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class);
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            int chunk = mapManger.getChunk(infMapComponent.infChunks, gameCoordinate.x, gameCoordinate.y);
            byte innerChunkX = MapManager.getInnerChunk(gameCoordinate.x);
            byte innerChunkY = MapManager.getInnerChunk(gameCoordinate.y);
            int topCellId = context.getSystem(MapManager.class).getTopCell(chunk, innerChunkX, innerChunkY);
            mapManger.addCellBeingMine(topCellId);
        } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            int chunk = mapManger.getChunk(infMapComponent.infChunks, gameCoordinate.x, gameCoordinate.y);
            int topCell = mapManger.getTopCell(chunk, MapManager.getInnerChunk(gameCoordinate.x), MapManager.getInnerChunk(gameCoordinate.y));
            InfCellComponent infCellComponent = mCell.get(topCell);
            if (infCellComponent.cellRegisterEntry.getCellBehavior().getInteragieClickDroit() != null) {
                infCellComponent.cellRegisterEntry.getCellBehavior().getInteragieClickDroit().accept(world, topCell);
            } else {
                if (mapManger.placeItemToBloc(context.getEcsEngine().getPlayerId(), Input.Buttons.RIGHT, infMapComponent.infChunks, gameCoordinate.x, gameCoordinate.y, context.getSystem(ItemBarManager.class).getSelectItem())) {
                    world.getSystem(InventoryManager.class).removeOneItem(context.getSystem(ItemBarManager.class).getSelectStack());
                }
            }
        }
    }
}
