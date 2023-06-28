package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.ecs.component.PlayerComponent;
import ch.realmtech.game.ecs.component.StoredItemComponent;
import ch.realmtech.input.InputMapper;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.utils.Array;

public class PlayerInventoryManager extends BaseSystem {
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<StoredItemComponent> mStoredItem;
    private ComponentMapper<InventoryComponent> mInventory;
    private Stage inventoryStage;
    private Window inventoryWindow;
    private Table inventoryTable;
    private Table craftingTable;
    private DragAndDrop dragAndDrop;
    @Wire(name = "context")
    private RealmTech context;

    @Override
    protected void processSystem() {
        inventoryStage.draw();
        //inventoryStage.setDebugAll(true);
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
        this.inventoryWindow = new Window("Inventaire", context.getSkin());
        this.inventoryTable = new Table(context.getSkin());
        this.craftingTable = new Table(context.getSkin());
        this.dragAndDrop = new DragAndDrop();
        dragAndDrop.setDragTime(0);
        inventoryWindow.add(inventoryTable);
        float with = inventoryStage.getWidth() * 0.5f;
        float height = inventoryStage.getHeight() * 0.5f;
        inventoryWindow.setBounds((inventoryStage.getWidth() - with) /2 ,(inventoryStage.getHeight() - height ) / 2, with, height);
        inventoryStage.addActor(inventoryWindow);
        setEnabled(false);
    }

    public void toggleInventoryWindow(int playerId){
        if (isEnabled()) {
            super.setEnabled(false);
            Gdx.input.setInputProcessor(context.getInputManager());
        } else {
            super.setEnabled(true);
            InputMapper.reset();
            Gdx.input.setInputProcessor(inventoryStage);
            clearDisplayInventory();
            displayInventory(playerId, inventoryTable);
            displayInventory(world.getSystem(TagManager.class).getEntityId("crafting"), inventoryTable);
            displayInventory(world.getSystem(TagManager.class).getEntityId("crafting-result-inventory"), inventoryTable);
        }
    }

    private void clearDisplayInventory() {
        inventoryTable.clear();
        dragAndDrop.clear();
    }

    /**
     * Ajout les cases de l'inventaire dans le tableau passé en second paramètre.
     * @param inventoryId L'id de l'inventaire.
     * @param inventoryTable La table où l'ont souhait affiché l'inventaire.
     */
    public void displayInventory(int inventoryId, Table inventoryTable) {
        Array<Table> cellsToDisplay = getCellsToDisplay(inventoryId);
        for (int i = 0; i < cellsToDisplay.size; i++) {
            if (i % mInventory.get(inventoryId).numberOfSlotParRow == 0) {
                inventoryTable.row().padBottom(2f);
            }
            inventoryTable.add(cellsToDisplay.get(i)).padLeft(2f);
        }
    }

    /**
     * Crée les cellules de l'inventaire.
     * @param IventoryId L'inventaire id.
     * @return Une liste de table qui sont les cellules avec la texture.
     */
    public Array<Table> getCellsToDisplay(int IventoryId) {
        Array<Table> itemSlots = new Array<>();
        InventoryComponent inventoryComponent = mInventory.get(IventoryId);
        int[][] inventory = inventoryComponent.inventory;
        for (int[] slotId : inventory) {
            Table itemSlotTable = new Table(context.getSkin());
            itemSlotTable.setBackground(new TextureRegionDrawable(inventoryComponent.backgroundTexture));
            final Image imageItem;
            if (slotId[0] != 0) {
                imageItem = new Image(mItem.get(slotId[0]).itemRegisterEntry.getTextureRegion());
            } else {
                imageItem = new Image(inventoryComponent.backgroundTexture);
            }
            itemSlotTable.add(imageItem);
            itemSlots.add(itemSlotTable);
            addDragAndDrop(itemSlotTable, imageItem, slotId);
        }
        return itemSlots;
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
            inventoryItem.slotTable.add(new Image());
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
            // TODO faire quelque chose de plus propre pour rafraichir l'inventaire que de le fermer de de le réouvrir
            manager.toggleInventoryWindow(manager.world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG));
            manager.toggleInventoryWindow(manager.world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG));
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
