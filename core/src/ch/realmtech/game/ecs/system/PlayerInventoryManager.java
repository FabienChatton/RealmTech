package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.ecs.component.PlayerComponent;
import ch.realmtech.game.ecs.component.StoredItemComponent;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class PlayerInventoryManager extends BaseSystem {
    private static TextureRegion defaultBackGroundTexture;
    private Stage inventoryStage;
    private Window inventoryWindow;
    private Table inventoryTable;
    private DragAndDrop dragAndDrop;
    @Wire(name = "context")
    private RealmTech context;

    @Override
    protected void processSystem() {
        inventoryStage.draw();
        inventoryStage.setDebugAll(true);
        int[][] inventory = context.getEcsEngine().getEcsWorld().getMapper(InventoryComponent.class).get(context.getPlayerId()).inventory;

    }
    /*
    inventoryStage
    inventoryWindow
    inventoryTable
     */
    @Override
    protected void initialize() {
        super.initialize();
        this.inventoryStage = new Stage(context.getUiStage().getViewport(), context.getUiStage().getBatch());
        this.inventoryWindow = new Window("player inventory", context.getSkin());
        this.inventoryTable = new Table(context.getSkin());
        this.dragAndDrop = new DragAndDrop();
        dragAndDrop.setDragTime(0);
        inventoryWindow.add(inventoryTable);
        float with = inventoryStage.getWidth() * 0.9f;
        float height = inventoryStage.getHeight() * 0.9f;
        inventoryWindow.setBounds((inventoryStage.getWidth() - with) /2 ,(inventoryStage.getHeight() - height ) / 2, with, height);
        inventoryStage.addActor(inventoryWindow);
        setEnabled(false);
        defaultBackGroundTexture = context.getTextureAtlas().findRegion("water-01");
    }

    public void toggleInventoryWindow(int playerId){
        if (isEnabled()) {
            super.setEnabled(false);
            Gdx.input.setInputProcessor(context.getInputManager());
        } else {
            super.setEnabled(true);
            Gdx.input.setInputProcessor(inventoryStage);
            setInventoryToDisplay(playerId);
        }
    }

    public void setInventoryToDisplay(int playerId) {
        inventoryTable.clear();
        dragAndDrop.clear();
        ComponentMapper<ItemComponent> mItem = context.getEcsEngine().getEcsWorld().getMapper(ItemComponent.class);
        ComponentMapper<StoredItemComponent> mStoredItem = context.getEcsEngine().getEcsWorld().getMapper(StoredItemComponent.class);

        int[][] inventory = context.getEcsEngine().getEcsWorld().getMapper(InventoryComponent.class).get(playerId).inventory;
        int row = 0;
        for (int[] slotId : inventory) {
            if (row % InventoryComponent.NUMBER_OF_SLOT_PAR_ROW == 0) {
                inventoryTable.row().padBottom(2f);
            }
            Table itemSlotTable = new Table(context.getSkin());
            itemSlotTable.setBackground(new TextureRegionDrawable(defaultBackGroundTexture));
            final Image imageItem;
            if (slotId[0] != 0) {
                imageItem = new Image(mItem.create(slotId[0]).itemRegisterEntry.getTextureRegion());
            } else {
                imageItem = new Image(defaultBackGroundTexture);
            }
            itemSlotTable.add(imageItem);
            inventoryTable.add(itemSlotTable).padLeft(2f);
            addDragAndDrop(itemSlotTable, imageItem, slotId);
            row++;
        }
    }

    private void addDragAndDrop(Table itemSlotTable, Image imageItem, int[] itemId) {
        dragAndDrop.addTarget(new InventoryTarget(new InventoryItem(imageItem, itemSlotTable, itemId), dragAndDrop, this));
        dragAndDrop.addSource(new InventorySource(new InventoryItem(imageItem, itemSlotTable, itemId), dragAndDrop, this));
    }
    static class InventoryItem {
        Image itemImage;
        Table slotTable;
        int[] slotId;

        public InventoryItem(Image itemImage, Table slotTable, int[] slotId) {
            this.itemImage = itemImage;
            this.slotTable = slotTable;
            this.slotId = slotId;
        }
    }
    static class InventorySource extends Source {
        final InventoryItem inventoryItem;
        final DragAndDrop dragAndDrop;
        final PlayerInventoryManager manager;

        public InventorySource(InventoryItem inventoryItem, DragAndDrop dragAndDrop, PlayerInventoryManager manager) {
            super(inventoryItem.itemImage);
            this.inventoryItem = inventoryItem;
            this.dragAndDrop = dragAndDrop;
            this.manager = manager;
        }

        @Override
        public Payload dragStart(InputEvent event, float x, float y, int pointer) {
            if (inventoryItem.slotId[0] == 0) return null;
            Payload payload = new Payload();
            payload.setObject(inventoryItem);
            payload.setDragActor(inventoryItem.itemImage);
            dragAndDrop.setDragActorPosition(inventoryItem.itemImage.getImageWidth() /2f, -inventoryItem.itemImage.getImageHeight()/2f);
            inventoryItem.slotTable.add(new Image(PlayerInventoryManager.defaultBackGroundTexture));
            manager.inventoryStage.addActor(inventoryItem.itemImage);
            return payload;
        }

        @Override
        public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
            super.dragStop(event, x, y, pointer, payload, target);
            InventoryItem inventoryItemSource = (InventoryItem) payload.getObject();
            InventoryTarget inventoryTarget = (InventoryTarget) target;
            if (inventoryTarget == null) {

            } else {
                if (inventoryTarget.inventoryItem.slotId[0] == 0) {
                    inventoryTarget.inventoryItem.slotId[0] = inventoryItemSource.slotId[0];
                    inventoryItemSource.slotId[0] = 0;
                }
            }
            payload.getDragActor().remove();
            manager.setInventoryToDisplay(manager.world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG));
        }
    }
    static class InventoryTarget extends Target {
        InventoryItem inventoryItem;
        final DragAndDrop dragAndDrop;
        final PlayerInventoryManager manager;

        public InventoryTarget(InventoryItem inventoryItem, DragAndDrop dragAndDrop, PlayerInventoryManager manager) {
            super(inventoryItem.itemImage);
            this.inventoryItem = inventoryItem;
            this.dragAndDrop = dragAndDrop;
            this.manager = manager;
        }

        @Override
        public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
            return true;
        }

        @Override
        public void drop(Source source, Payload payload, float x, float y, int pointer) {
        }
    }
}
