package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.clickAndDrop.ClickActorAndSlot;
import ch.realmtech.game.clickAndDrop.ClickAndDrop;
import ch.realmtech.game.clickAndDrop.ClickAndDropEvent;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class PlayerInventorySystem extends BaseSystem {
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<StoredItemComponent> mStoredItem;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<ItemResultCraftComponent> mItemResultCraft;
    private Stage inventoryStage;
    private Window inventoryWindow;
    private Table inventoryTable;
    private Table craftingTable;
    @Wire(name = "context")
    private RealmTech context;
    private Skin skin;
    private ClickAndDrop clickAndDrop;

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
        this.skin = context.getSkin();
        inventoryWindow.add(inventoryTable);
        float with = inventoryStage.getWidth() * 0.5f;
        float height = inventoryStage.getHeight() * 0.5f;
        inventoryWindow.setBounds((inventoryStage.getWidth() - with) /2 ,(inventoryStage.getHeight() - height ) / 2, with, height);
        inventoryStage.addActor(inventoryWindow);
        setEnabled(false);
        this.clickAndDrop = new ClickAndDrop(context);
    }

    public void toggleInventoryWindow(){
        if (isEnabled()) {
            super.setEnabled(false);
            clickAndDrop.clear();
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
                itemCount = new Label(Integer.toString(InventoryManager.tailleStack(stack)), skin);
                addClickAndDropSrc(imageTable, stack);
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

            addClickAndDropDst(imageTable, stack);
        }
        return itemSlots;
    }
    private void addClickAndDropSrc(Actor imageItem, int[] stack) {
        imageItem.setTouchable(Touchable.enabled);

        clickAndDrop.addSource(new ClickActorAndSlot(imageItem, stack), new ClickAndDropEvent() {
            @Override
            public boolean clickStart(ClickActorAndSlot clickActorAndSlot) {
                return true;
            }

            @Override
            public boolean clickStop(ClickActorAndSlot actorSrc, ClickActorAndSlot actorDst) {
                boolean ret;
                if (actorDst != null) {
                    ret = world.getSystem(InventoryManager.class).moveStackToStack(actorSrc.stack, actorDst.stack);
                    refreshPlayerInventory();
                    if (mItemResultCraft.has(actorDst.stack[0])) {
                        mItemResultCraft.get(actorDst.stack[0]).pickEvent.pick(world, mInventory.get(world.getSystem(TagManager.class).getEntityId("crafting")));
                        world.edit(actorDst.stack[0]).remove(ItemResultCraftComponent.class);
                    }
                    return ret;
                }
                return false;
            }
        });
    }

    public void addClickAndDropDst(Actor imageItem, int[] stack) {
        clickAndDrop.addTarget(new ClickActorAndSlot(imageItem, stack));
    }
}
