package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.ecs.component.ItemResultCraftComponent;
import ch.realmtech.game.ecs.component.StoredItemComponent;
import ch.realmtech.game.registery.ItemRegisterEntry;
import ch.realmtech.input.InputMapper;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;

public class PlayerInventorySystem extends BaseSystem {
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<StoredItemComponent> mStoredItem;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<ItemResultCraftComponent> mItemResultCraft;
    private Stage inventoryStage;
    private Window inventoryWindow;
    private Table inventoryTable;
    private Table craftingTable;
    private DragAndDrop dragAndDrop;
    @Wire(name = "context")
    private RealmTech context;
    private Skin skin;

    @Override
    protected void processSystem() {
        inventoryStage.draw();
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
        this.skin = context.getSkin();
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
            refreshPlayerInventory();
        }
    }

    private void displayPlayerInventory() {
        displayInventory(context.getEcsEngine().getPlayerId(), inventoryTable);
        displayInventory(world.getSystem(TagManager.class).getEntityId("crafting"), inventoryTable);
        displayInventory(world.getSystem(TagManager.class).getEntityId("crafting-result-inventory"), inventoryTable);
    }
    public void refreshPlayerInventory() {
        clearDisplayInventory();
        displayPlayerInventory();
    }

    public void nouveauCraftDisponible(ItemRegisterEntry craftResult) {
        if (!mItem.has(mInventory.get(world.getSystem(TagManager.class).getEntityId("crafting-result-inventory")).inventory[0][0])) {
            int itemResultId = world.getSystem(ItemManager.class).newItemInventory(craftResult);
            world.edit(itemResultId).create(ItemResultCraftComponent.class);
            world.getSystem(InventoryManager.class).addItemToInventory(itemResultId, world.getSystem(TagManager.class).getEntityId("crafting-result-inventory"));
            refreshPlayerInventory();
        }
    }

    public void aucunCraftDisponible() {
        int[][] inventory = mInventory.get(world.getSystem(TagManager.class).getEntityId("crafting-result-inventory")).inventory;
        int itemCraftResultId = inventory[0][0];
        if (mItem.has(itemCraftResultId)) {
            world.getSystem(InventoryManager.class).clearInventory(inventory);
            world.delete(itemCraftResultId);
            refreshPlayerInventory();
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
        Array<Table> cellsToDisplay = getItemSlotsToDisplay(inventoryId);
        for (int i = 0; i < cellsToDisplay.size; i++) {
            if (i % mInventory.get(inventoryId).numberOfSlotParRow == 0) {
                inventoryTable.row().padBottom(2f);
            }
            inventoryTable.add(cellsToDisplay.get(i)).padLeft(2f);
        }
    }

    /**
     * Crées les items slots de l'inventaire.
     * @param inventoryId L'inventaire id.
     * @return Une liste de table qui sont les item slots
     */
    public Array<Table> getItemSlotsToDisplay(int inventoryId) {
        Array<Table> itemSlots = new Array<>();
        InventoryComponent inventoryComponent = mInventory.get(inventoryId);
        int[][] inventory = inventoryComponent.inventory;
        for (int[] stack : inventory) {
            Table itemSlotTable = new Table(context.getSkin());
            itemSlotTable.setBackground(new TextureRegionDrawable(inventoryComponent.backgroundTexture));
            final Table imageTable = new Table();
            final Image imageItem;
            final Label itemCount;
            if (stack[0] != 0) {
                imageItem = new Image(mItem.get(stack[0]).itemRegisterEntry.getTextureRegion());
                final long count = Arrays.stream(stack).filter(value -> value != 0).count();
                itemCount = new Label(Long.toString(count), skin);
            } else {
                imageItem = new Image(inventoryComponent.backgroundTexture);
                itemCount = new Label(null, skin);
            }
            itemCount.setFontScale(0.5f);
            imageTable.add(imageItem);
            imageTable.addActor(itemCount);
            itemCount.moveBy(0, imageItem.getHeight() / 2 - 10);

            itemSlotTable.add(imageTable);
            itemSlots.add(itemSlotTable);
            addDragAndDrop(itemSlotTable, imageTable, stack);
        }
        return itemSlots;
    }
    private void addDragAndDrop(Table itemSlotTable, Actor imageItem, int[] itemId) {
        dragAndDrop.addTarget(new InventoryTarget(new InventoryItem(imageItem, itemSlotTable, itemId), dragAndDrop, this));
        dragAndDrop.addSource(new InventorySource(new InventoryItem(imageItem, itemSlotTable, itemId), dragAndDrop, this));
    }
    static class InventoryItem {
        Actor itemImage;
        Table slotTable;

        int[] slotId;
        public InventoryItem(Actor itemImage, Table slotTable, int[] slotId) {
            this.itemImage = itemImage;
            this.slotTable = slotTable;
            this.slotId = slotId;
        }
    }
    static class InventorySource extends Source {
        final InventoryItem inventoryItem;
        final DragAndDrop dragAndDrop;
        final PlayerInventorySystem playerInventorySystem;

        public InventorySource(InventoryItem inventoryItem, DragAndDrop dragAndDrop, PlayerInventorySystem playerInventorySystem) {
            super(inventoryItem.itemImage);
            this.inventoryItem = inventoryItem;
            this.dragAndDrop = dragAndDrop;
            this.playerInventorySystem = playerInventorySystem;
        }

        @Override
        public Payload dragStart(InputEvent event, float x, float y, int pointer) {
            if (inventoryItem.slotId[0] == 0) return null;
            Payload payload = new Payload();
            payload.setObject(inventoryItem);
            payload.setDragActor(inventoryItem.itemImage);
            dragAndDrop.setDragActorPosition(inventoryItem.itemImage.getWidth() /2f, -inventoryItem.itemImage.getHeight() / 2f);
            playerInventorySystem.inventoryStage.addActor(inventoryItem.itemImage);
            return payload;
        }
        @Override
        public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
            super.dragStop(event, x, y, pointer, payload, target);
            InventoryItem inventoryItemSource = (InventoryItem) payload.getObject();
            InventoryTarget inventoryTarget = (InventoryTarget) target;
            // si c'est le résultat d'un craft
            if (playerInventorySystem.mItemResultCraft.has(inventoryItem.slotId[0])) {
                playerInventorySystem.mItemResultCraft.get(inventoryItem.slotId[0]).pickEvent.pick(playerInventorySystem.world, playerInventorySystem.mInventory.get(playerInventorySystem.world.getSystem(TagManager.class).getEntityId("crafting")));
                playerInventorySystem.world.edit(inventoryItem.slotId[0]).remove(ItemResultCraftComponent.class);
            }
            if (inventoryTarget == null) {
                System.out.println("je devrais être drop");
            } else {
                if (playerInventorySystem.world.getSystem(InventoryManager.class).moveStackToStack(inventoryItemSource.slotId, inventoryTarget.inventoryItem.slotId)) {
                    System.out.println("deplace");
                } else {
                    System.out.println("pas déplace");
                }
            }
            payload.getDragActor().remove();
            playerInventorySystem.refreshPlayerInventory();
        }

    }

    static class InventoryTarget extends Target {
        InventoryItem inventoryItem;
        final DragAndDrop dragAndDrop;
        final PlayerInventorySystem manager;

        public InventoryTarget(InventoryItem inventoryItem, DragAndDrop dragAndDrop, PlayerInventorySystem manager) {
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
