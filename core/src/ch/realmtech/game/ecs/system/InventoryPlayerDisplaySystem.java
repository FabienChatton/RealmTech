package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.PlayerComponent;
import ch.realmtech.game.ecs.component.TextureComponent;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

public class InventoryPlayerDisplaySystem extends BaseSystem {
    @Wire(name = "context")
    private RealmTech context;
    private Stage inventoryStage;
    private Window playerInventoryWindow;

    @Override
    protected void processSystem() {
        inventoryStage.draw();
    }

    @Override
    protected void initialize() {
        super.initialize();
        inventoryStage = new Stage(context.getUiStage().getViewport());
    }

    public void togglePlayerInventoryWindow() {
        if (playerInventoryWindow == null) {

            ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);
            ComponentMapper<TextureComponent> mTexture = world.getMapper(TextureComponent.class);

            int playerId = world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG);
            InventoryComponent inventoryComponent = mInventory.get(playerId);
            playerInventoryWindow = new Window("Inventaire", context.getSkin());
            playerInventoryWindow.setBounds(inventoryStage.getWidth() / 2 - 150, inventoryStage.getHeight() / 2 - 150, 300, 300);
            for (int item : inventoryComponent.inventory.getData()) {
                if (item == 0) continue;
                TextureComponent textureComponent = mTexture.get(item);
                if (textureComponent != null) {
                    DragAndDrop dragAndDrop = new DragAndDrop();
                    Image itemImage = new Image(textureComponent.texture);
                    dragAndDrop.addSource(new DragAndDrop.Source(itemImage) {
                        @Override
                        public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                            DragAndDrop.Payload payload = new DragAndDrop.Payload();
                            payload.setDragActor(getActor());
                            inventoryStage.addActor(getActor());
                            dragAndDrop.setDragActorPosition(getActor().getWidth() / 2, -getActor().getHeight() / 2);
                            return payload;
                        }

                        @Override
                        public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                            inventoryStage.getActors().removeValue(getActor(), true);
                            playerInventoryWindow.removeActor(payload.getDragActor());
                            Vector3 worldPosition = context.getGameStage().getCamera().unproject(new Vector3(
                                    Gdx.input.getX() - payload.getDragActor().getWidth() / 2,
                                    Gdx.input.getY() + payload.getDragActor().getHeight() / 2,
                                    0));

                            world.getSystem(InventoryManager.class).dropItemFromPlayerInventory(
                                    item,
                                    world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG),
                                    worldPosition.x,
                                    worldPosition.y);
                        }
                    });
                    dragAndDrop.addTarget(new DragAndDrop.Target(playerInventoryWindow) {
                        @Override
                        public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                            return true;
                        }

                        @Override
                        public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                            Gdx.app.postRunnable(() -> playerInventoryWindow.add((source.getActor())));
                        }
                    });
                    playerInventoryWindow.add(itemImage);
                }
            }
            inventoryStage.addActor(playerInventoryWindow);
        } else {
            inventoryStage.getActors().removeValue(playerInventoryWindow, true);
            playerInventoryWindow = null;
        }
    }

    public boolean isDisplay(){
        return playerInventoryWindow != null;
    }

    public void setInputProcessor() {
        Gdx.input.setInputProcessor(inventoryStage);
    }
}
