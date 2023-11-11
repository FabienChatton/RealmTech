package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.plgin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.InfCellComponent;
import ch.realmtech.server.ecs.component.InfMapComponent;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.ecs.system.MapManager;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

public class PlayerInputSystem extends BaseSystem {
    @Wire(name = "context")
    private RealmTech context;
    @Wire
    private SystemsAdminClient systemsAdminClient;
    private ComponentMapper<InfCellComponent> mCell;

    @Override
    protected void processSystem() {
        Vector2 screenCoordinate = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector2 gameCoordinate = context.getEcsEngine().getGameCoordinate(screenCoordinate);
        int worldPosX = MapManager.getWorldPos(gameCoordinate.x);
        int worldPosY = MapManager.getWorldPos(gameCoordinate.y);

        InfMapComponent infMapComponent = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class);
        int chunk = systemsAdminClient.mapManager.getChunk(MapManager.getChunkPos(worldPosX), MapManager.getChunkPos(worldPosY), infMapComponent.infChunks);
        if (chunk == -1) return;
        byte innerChunkX = MapManager.getInnerChunk(worldPosX);
        byte innerChunkY = MapManager.getInnerChunk(worldPosY);
        int topCell = systemsAdminClient.mapManager.getTopCell(chunk, innerChunkX, innerChunkY);
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            systemsAdminClient.mapManager.addCellBeingMine(topCell);
        } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            InfCellComponent infCellComponent = mCell.get(topCell);
            if (infCellComponent.cellRegisterEntry.getCellBehavior().getInteragieClickDroit() != null) {
                infCellComponent.cellRegisterEntry.getCellBehavior().getInteragieClickDroit().accept(world, topCell);
            } else {
                if (systemsAdminClient.mapManager.placeItemToBloc(context.getEcsEngine().getPlayerId(), Input.Buttons.RIGHT, infMapComponent.infChunks, gameCoordinate.x, gameCoordinate.y, systemsAdminClient.itemBarManager.getSelectItem())) {
                    systemsAdminClient.inventoryManager.deleteOneItem(systemsAdminClient.itemBarManager.getSelectStack());
                }
            }
        }
    }
}