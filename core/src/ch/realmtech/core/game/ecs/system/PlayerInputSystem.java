package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.CellComponent;
import ch.realmtech.server.ecs.component.InfMapComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.level.ClickEventClient;
import ch.realmtech.server.level.ClickInteraction;
import ch.realmtech.server.packet.serverPacket.ItemToCellPlaceRequestPacket;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import java.util.Optional;
import java.util.UUID;

public class PlayerInputSystem extends BaseSystem {
    @Wire(name = "context")
    private RealmTech context;
    @Wire
    private SystemsAdminClient systemsAdminClient;
    private ComponentMapper<CellComponent> mCell;
    private ComponentMapper<ItemComponent> mItem;

    @Override
    protected void processSystem() {
        Vector2 screenCoordinate = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector2 gameCoordinate = context.getEcsEngine().getGameCoordinate(screenCoordinate);
        int worldPosX = MapManager.getWorldPos(gameCoordinate.x);
        int worldPosY = MapManager.getWorldPos(gameCoordinate.y);

        InfMapComponent infMapComponent = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class);
        int chunk = systemsAdminClient.getMapManager().getChunk(MapManager.getChunkPos(worldPosX), MapManager.getChunkPos(worldPosY), infMapComponent.infChunks);
        if (chunk == -1) return;
        byte innerChunkX = MapManager.getInnerChunk(worldPosX);
        byte innerChunkY = MapManager.getInnerChunk(worldPosY);
        int topCell = systemsAdminClient.getMapManager().getTopCell(chunk, innerChunkX, innerChunkY);
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            int selectItem = systemsAdminClient.getItemBarManager().getSelectItem();
            Optional<ClickInteraction> leftClickInteractionItemSelected = Optional.empty();
            if (mItem.has(selectItem)) {
                leftClickInteractionItemSelected = Optional.ofNullable(mItem.get(selectItem).itemRegisterEntry.getLeftClickInteraction())
                        .map((leftClickInteractionItemClient) -> leftClickInteractionItemClient.toClickInteraction(new ClickEventClient(screenCoordinate.x, screenCoordinate.y, gameCoordinate.x, gameCoordinate.y), topCell));
            }

            // item right click first, or mine top cell
            leftClickInteractionItemSelected.ifPresentOrElse((clickInteraction) -> clickInteraction.accept(context, topCell), () -> {
                systemsAdminClient.getMapManager().addCellBeingMine(topCell);
            });

        } else if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            int selectItem = systemsAdminClient.getItemBarManager().getSelectItem();
            Optional<ClickInteraction> rightClickInteractionItemSelected = Optional.empty();
            CellComponent cellComponent = mCell.get(topCell);
            Optional<ClickInteraction> rightClickInteractionCell = cellComponent.cellRegisterEntry.getCellBehavior().getInteragieClickDroit();

            if (mItem.has(selectItem)) {
                rightClickInteractionItemSelected = Optional.ofNullable(mItem.get(selectItem).itemRegisterEntry.getRightClickInteraction())
                        .map((rightClickInteractionItemClient) -> rightClickInteractionItemClient.toClickInteraction(new ClickEventClient(screenCoordinate.x, screenCoordinate.y, gameCoordinate.x, gameCoordinate.y), topCell));
            }

            // item interaction first, and if not, cell interaction
            rightClickInteractionItemSelected
                    .or(() -> rightClickInteractionCell)
                    .ifPresentOrElse((clickInteraction) -> clickInteraction.accept(context, topCell), () -> {
                        if (selectItem != 0) {
                            // place bloc if no interaction
                            UUID itemUuid = systemsAdminClient.getUuidEntityManager().getEntityUuid(selectItem);
                            context.getClientConnexion().sendAndFlushPacketToServer(new ItemToCellPlaceRequestPacket(itemUuid, worldPosX, worldPosY));
                        }
                    });
        }
    }
}
