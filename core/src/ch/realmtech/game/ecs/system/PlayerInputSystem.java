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
import com.badlogic.gdx.math.Vector2;

public class PlayerInputSystem extends BaseSystem {
    @Wire(name = "context")
    private RealmTech context;
    private ComponentMapper<InfCellComponent> mCell;

    @Override
    protected void processSystem() {
        Vector2 screenCoordinate = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector2 gameCoordinate = context.getEcsEngine().getGameCoordinate(screenCoordinate);
        int worldPosX = MapManager.getWorldPos(gameCoordinate.x);
        int worldPosY = MapManager.getWorldPos(gameCoordinate.y);

        MapManager mapManger = world.getSystem(MapManager.class);
        InfMapComponent infMapComponent = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class);
        int chunk = mapManger.getChunk(MapManager.getChunkPos(worldPosX), MapManager.getChunkPos(worldPosY), infMapComponent.infChunks);
        if (chunk == -1) return;
        byte innerChunkX = MapManager.getInnerChunk(worldPosX);
        byte innerChunkY = MapManager.getInnerChunk(worldPosY);
        int topCell = context.getSystem(MapManager.class).getTopCell(chunk, innerChunkX, innerChunkY);
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            mapManger.addCellBeingMine(topCell);
        } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            InfCellComponent infCellComponent = mCell.get(topCell);
            if (infCellComponent.cellRegisterEntry.getCellBehavior().getInteragieClickDroit() != null) {
                infCellComponent.cellRegisterEntry.getCellBehavior().getInteragieClickDroit().accept(world, topCell);
            } else {
                if (mapManger.placeItemToBloc(context.getEcsEngine().getPlayerId(), Input.Buttons.RIGHT, infMapComponent.infChunks, gameCoordinate.x, gameCoordinate.y, context.getSystem(ItemBarManager.class).getSelectItem())) {
                    world.getSystem(InventoryManager.class).deleteOneItem(context.getSystem(ItemBarManager.class).getSelectStack());
                }
            }
        }
    }
}
