package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.clickAndDrop.ClickActorAndSlot;
import ch.realmtech.game.clickAndDrop.ClickAndDrop;
import ch.realmtech.game.clickAndDrop.ClickAndDropEvent;
import ch.realmtech.game.clickAndDrop.ImageItemTable;
import ch.realmtech.game.clickAndDrop.clickAndDrop2.ClickAndDrop2;
import ch.realmtech.game.clickAndDrop.clickAndDrop2.ClickAndDropActor;
import ch.realmtech.game.craft.CraftResult;
import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.ecs.component.ItemResultCraftComponent;
import ch.realmtech.game.ecs.component.StoredItemComponent;
import ch.realmtech.input.InputMapper;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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
    private ClickAndDrop2 clickAndDrop2;

    @Override
    protected void processSystem() {
        inventoryStage.act();
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
        this.clickAndDrop2 = new ClickAndDrop2(inventoryStage, world);
        //inventoryStage.setDebugAll(true);
        inventoryWindow.setTouchable(Touchable.disabled);
    }

    public void toggleInventoryWindow(){
        if (isEnabled()) {
            super.setEnabled(false);
            clickAndDrop.clear();
            Gdx.input.setInputProcessor(context.getInputManager());
        } else {
            super.setEnabled(true);
            InputMapper.reset();
            refreshPlayerInventory();
        }
    }

    private void displayPlayerInventory() {
        displayInventory(context.getEcsEngine().getPlayerId(), inventoryTable, true, true);
        displayInventory(world.getSystem(TagManager.class).getEntityId("crafting"), inventoryTable, true, true);
        displayInventory(world.getSystem(TagManager.class).getEntityId("crafting-result-inventory"), inventoryTable, true, false);
    }
    public void refreshPlayerInventory() {
        clearDisplayInventory();
        displayPlayerInventory();
        Gdx.input.setInputProcessor(inventoryStage);
    }

    public void nouveauCraftDisponible(CraftResult craftResult) {
        final int[][] inventory = mInventory.get(world.getSystem(TagManager.class).getEntityId("crafting-result-inventory")).inventory;
        if (!mItem.has(inventory[0][0])) {
            for (int i = 0; i < craftResult.nombre(); i++) {
                int itemResultId = world.getSystem(ItemManager.class).newItemInventory(craftResult.itemRegisterEntry());
                world.edit(itemResultId).create(ItemResultCraftComponent.class);
                world.getSystem(InventoryManager.class).addItemToInventory(itemResultId, world.getSystem(TagManager.class).getEntityId("crafting-result-inventory"));
            }
            refreshPlayerInventory();
        }
    }

    public void aucunCraftDisponible() {
        int[][] inventory = mInventory.get(world.getSystem(TagManager.class).getEntityId("crafting-result-inventory")).inventory;
        if (inventory[0][0] != 0) {
            world.getSystem(InventoryManager.class).removeInventory(inventory);
        }
    }

    private void clearDisplayInventory() {
        inventoryTable.clear();
        clickAndDrop.clear();
    }

    /**
     * Ajout les cases de l'inventaire dans le tableau passé en second paramètre.
     *
     * @param inventoryId     L'id de l'inventaire.
     * @param inventoryTable  La table où l'ont souhait affiché l'inventaire.
     * @param clickAndDropSrc
     * @param clickAndDropDst
     */
    public void displayInventory(int inventoryId, Table inventoryTable, boolean clickAndDropSrc, boolean clickAndDropDst) {
//        Array<Table> cellsToDisplay = createItemSlotsToDisplay(inventoryId, clickAndDropSrc, clickAndDropDst);
//        for (int i = 0; i < cellsToDisplay.size; i++) {
//            if (i % mInventory.get(inventoryId).numberOfSlotParRow == 0) {
//                inventoryTable.row().padBottom(2f);
//            }
//            inventoryTable.add(cellsToDisplay.get(i)).padLeft(2f);
//        }
        Array<Table> tableImages = createItemSlotsToDisplay(inventoryId);
        for (int i = 0; i < tableImages.size; i++) {
            if (i % mInventory.get(inventoryId).numberOfSlotParRow == 0) {
                inventoryTable.row().padBottom(2f);
            }
            inventoryTable.add(tableImages.get(i)).padLeft(2f);
        }
    }

    public Array<Table> createItemSlotsToDisplay(int inventoryId) {
        final Array<Table> tableImages = new Array<>();
        InventoryComponent inventoryComponent = mInventory.get(inventoryId);
        int[][] inventory = inventoryComponent.inventory;
        for (int[] stack : inventory) {
            final Table tableImage = new Table();
            tableImage.setBackground(new TextureRegionDrawable(inventoryComponent.backgroundTexture));
            tableImage.setWidth(inventoryComponent.backgroundTexture.getRegionWidth());
            tableImage.setHeight(inventoryComponent.backgroundTexture.getRegionHeight());
            final ClickAndDropActor clickAndDropActor = new ClickAndDropActor(stack, mItem, tableImage);
            clickAndDropActor.setWidth(inventoryComponent.backgroundTexture.getRegionWidth());
            clickAndDropActor.setHeight(inventoryComponent.backgroundTexture.getRegionHeight());
            clickAndDrop2.addSource(clickAndDropActor);
            clickAndDrop2.addDestination(clickAndDropActor);
            inventoryStage.addActor(clickAndDropActor);
            inventoryStage.addActor(tableImage);
            tableImages.add(tableImage);
        }
        return tableImages;
    }
    
    private void addClickAndDropSrc(ImageItemTable imageItem, int[] stack) {
        clickAndDrop.addSource(new ClickActorAndSlot(imageItem), new ClickAndDropEvent() {
            @Override
            public ImageItemTable clickStart(ClickActorAndSlot clickActorAndSlot, int[] stackActive, InputEvent event) {
                ImageItemTable ret = null;
                if (event.getButton() == 1 && InventoryManager.tailleStack(stack) != 1) {
                    int nombreADeplacer = InventoryManager.tailleStack(stack) / 2;
                    // click droit, déplace la moitié du stack
                    ret = itemAvecCount(stack, nombreADeplacer);
                    context.getEcsEngine().getWorld().getSystem(InventoryManager.class).moveStackToStackNumber(stack, stackActive, nombreADeplacer);
                    final ImageItemTable imageItemTable = itemAvecCount(clickActorAndSlot.getStack(), InventoryManager.tailleStack(clickActorAndSlot.getStack()));
                    clickActorAndSlot.actor.setImage(imageItemTable.getImage());
                    clickActorAndSlot.actor.getImage().moveBy(imageItemTable.getImage().getWidth() / 2, imageItemTable.getImage().getHeight() / 2);
                    clickActorAndSlot.actor.setCountLabel(imageItemTable.getCountLabel());
                    clickActorAndSlot.actor.getCountLabel().moveBy(imageItemTable.getImage().getWidth() / 2, -7);
                } else {
                    // déplace toute la stack
                    ret = itemAvecCount(stack, InventoryManager.tailleStack(stack));
                    context.getEcsEngine().getWorld().getSystem(InventoryManager.class).moveStackToStack(stack, stackActive);
                    clickActorAndSlot.actor.image.moveBy(Integer.MAX_VALUE, Integer.MAX_VALUE);
                    clickActorAndSlot.actor.countLabel.moveBy(Integer.MAX_VALUE, Integer.MAX_VALUE);
                }
                if (mItemResultCraft.has(stackActive[0])) {
                    mItemResultCraft.get(stackActive[0]).pickEvent.pick(world, mInventory.get(world.getSystem(TagManager.class).getEntityId("crafting")));
                    world.edit(stackActive[0]).remove(ItemResultCraftComponent.class);
                }
                return ret;
            }

            @Override
            public ImageItemTable clickStop(ClickActorAndSlot clickActorAndSlotSrc, int[] stackActive, ClickActorAndSlot clickActorAndSlotDst, int button) {
                ImageItemTable ret = null;
                if (clickActorAndSlotDst != null) {
                    if (button == 0) {
                        world.getSystem(InventoryManager.class).moveStackToStack(stackActive, clickActorAndSlotDst.getStack());
                        ret = null;
                    } else {
                        world.getSystem(InventoryManager.class).moveStackToStackNumber(stackActive, clickActorAndSlotDst.getStack(), 1);
                        final int count = InventoryManager.tailleStack(stackActive);
                        if (count > 0) {
                            ret = itemAvecCount(stackActive, count);
                            final ImageItemTable imageItemTable = itemAvecCount(clickActorAndSlotDst.getStack(), InventoryManager.tailleStack(clickActorAndSlotDst.getStack()));
                            clickActorAndSlotDst.actor.setImage(imageItemTable.getImage());
                            clickActorAndSlotDst.actor.getImage().moveBy(imageItemTable.getImage().getWidth() / 2, imageItemTable.getImage().getHeight() / 2);
                            clickActorAndSlotDst.actor.setCountLabel(imageItemTable.getCountLabel());
                            clickActorAndSlotDst.actor.getCountLabel().moveBy(imageItemTable.getImage().getWidth() / 2, -7);
                        } else {
                            ret = null;
                        }
                    }


                }
                return ret;
            }
        });
    }

    public void addClickAndDropDst(ImageItemTable imageItem) {
        clickAndDrop.addTarget(new ClickActorAndSlot(imageItem));
    }

    private ImageItemTable itemAvecCount(int[] stack, int count) {
        ImageItemTable imageItemTable = new ImageItemTable(skin, stack);
        Image imageItem;
        Label itemCount;
        if (mItem.has(stack[0])) {
            imageItem = new Image(mItem.get(stack[0]).itemRegisterEntry.getTextureRegion());
            itemCount = new Label(Integer.toString(count), skin);
            imageItemTable.setImage(imageItem);
            imageItemTable.setCountLabel(itemCount);
        }
        return imageItemTable;
    }
}
