package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.component.MainPlayerComponent;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.core.screen.GameScreen;
import ch.realmtech.server.ecs.component.CellComponent;
import ch.realmtech.server.ecs.component.InfMapComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.component.PlayerDeadComponent;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.level.ClickEventClient;
import ch.realmtech.server.level.ClickInteraction;
import ch.realmtech.server.packet.serverPacket.ItemToCellPlaceRequestPacket;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import java.util.Optional;
import java.util.UUID;

@All(MainPlayerComponent.class)
@Exclude(PlayerDeadComponent.class)
public class PlayerInputSystem extends IteratingSystem {
    @Wire(name = "context")
    private RealmTech context;
    @Wire
    private SystemsAdminClient systemsAdminClient;
    private ComponentMapper<CellComponent> mCell;
    private ComponentMapper<ItemComponent> mItem;

    @Override
    protected void process(int entityId) {
        if (context.getScreen() instanceof GameScreen gameScreen) {
            if (!gameScreen.canInteractWithWorld()) {
                return;
            }
        }
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
        int selectItem = systemsAdminClient.getItemBarManager().getSelectItem();
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (mItem.has(selectItem)) {
                Optional.ofNullable(mItem.get(selectItem).itemRegisterEntry.getLeftClickOnJustPressed())
                        .map((leftClickInteractionItemClient) -> leftClickInteractionItemClient.toClickInteraction(new ClickEventClient(screenCoordinate.x, screenCoordinate.y, gameCoordinate.x, gameCoordinate.y)))
                        .ifPresent((clickInteraction) -> clickInteraction.accept(context, topCell, selectItem));
            }

        } else if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {

            if ((mItem.has(selectItem) && mItem.get(selectItem).itemRegisterEntry.getLeftClickOnJustPressed() != null)) {
                return;
            }

            Optional<ClickInteraction> leftClickInteractionItemSelected = Optional.empty();
            if (mItem.has(selectItem)) {
                leftClickInteractionItemSelected = Optional.ofNullable(mItem.get(selectItem).itemRegisterEntry.getLeftClickOnPressed())
                        .map((leftClickInteractionItemClient) -> leftClickInteractionItemClient.toClickInteraction(new ClickEventClient(screenCoordinate.x, screenCoordinate.y, gameCoordinate.x, gameCoordinate.y)));
            }

            // item right click first, or mine top cell
            leftClickInteractionItemSelected.ifPresentOrElse((clickInteraction) -> clickInteraction.accept(context, topCell, selectItem), () -> {
                systemsAdminClient.getMapManager().addCellBeingMine(topCell);
            });

        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            Optional<ClickInteraction> rightClickInteractionItemSelected = Optional.empty();
            CellComponent cellComponent = mCell.get(topCell);
            Optional<ClickInteraction> rightClickInteractionCell = cellComponent.cellRegisterEntry.getCellBehavior().getInteragieClickDroit();

            if (mItem.has(selectItem)) {
                rightClickInteractionItemSelected = Optional.ofNullable(mItem.get(selectItem).itemRegisterEntry.getRightClickOnJustPressed())
                        .map((rightClickInteractionItemClient) -> rightClickInteractionItemClient.toClickInteraction(new ClickEventClient(screenCoordinate.x, screenCoordinate.y, gameCoordinate.x, gameCoordinate.y)));
            }

            // item interaction first, and if not, cell interaction
            rightClickInteractionItemSelected
                    .or(() -> rightClickInteractionCell)
                    .ifPresentOrElse((clickInteraction) -> clickInteraction.accept(context, topCell, selectItem), () -> {
                        if (selectItem != 0) {
                            // place bloc if no interaction
                            UUID itemUuid = systemsAdminClient.getUuidEntityManager().getEntityUuid(selectItem);
                            context.getClientConnexion().sendAndFlushPacketToServer(new ItemToCellPlaceRequestPacket(itemUuid, worldPosX, worldPosY));
                        }
                    });
        } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            if (mItem.has(selectItem)) {
                Optional.ofNullable(mItem.get(selectItem).itemRegisterEntry.getRightClickOnPressed())
                        .map((leftClickInteractionItemClient) -> leftClickInteractionItemClient.toClickInteraction(new ClickEventClient(screenCoordinate.x, screenCoordinate.y, gameCoordinate.x, gameCoordinate.y)))
                        .ifPresent((clickInteraction) -> clickInteraction.accept(context, topCell, selectItem));
            }
        }
    }
}
