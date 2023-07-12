package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.clickAndDrop.ClickAndDrop2;
import ch.realmtech.game.clickAndDrop.ClickAndDropActor;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
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
        this.inventoryWindow = new Window("Inventaire", context.getSkin()) {
            @Override
            public void act(float delta) {
                super.act(delta);
                setZIndex(0);
            }
        };
        this.inventoryTable = new Table(context.getSkin());
        this.craftingTable = new Table(context.getSkin());
        this.skin = context.getSkin();
        inventoryWindow.add(inventoryTable);
        float with = inventoryStage.getWidth() * 0.5f;
        float height = inventoryStage.getHeight() * 0.5f;
        inventoryWindow.setBounds((inventoryStage.getWidth() - with) /2 ,(inventoryStage.getHeight() - height ) / 2, with, height);
        inventoryStage.addActor(inventoryWindow);
        setEnabled(false);
        this.clickAndDrop2 = new ClickAndDrop2(inventoryStage, world);
        //inventoryWindow.setTouchable(Touchable.disabled);
    }

    public void toggleInventoryWindow(){
        if (isEnabled()) {
            super.setEnabled(false);
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
        clickAndDrop2.clearActor();
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
        Array<Table> tableImages = createItemSlotsToDisplay(inventoryId, inventoryStage, clickAndDropSrc, clickAndDropDst);
        for (int i = 0; i < tableImages.size; i++) {
            if (i % mInventory.get(inventoryId).numberOfSlotParRow == 0) {
                inventoryTable.row().padBottom(2f);
            }
            inventoryTable.add(tableImages.get(i)).padLeft(2f);
        }
    }

    public Array<Table> createItemSlotsToDisplay(int inventoryId, Stage stage, boolean clickAndDropSrc, boolean clickAndDropDst) {
        final Array<Table> tableImages = new Array<>();
        InventoryComponent inventoryComponent = mInventory.get(inventoryId);
        int[][] inventory = inventoryComponent.inventory;
        for (int[] stack : inventory) {
            final Table tableImage = new Table();
            tableImage.setBackground(new TextureRegionDrawable(inventoryComponent.backgroundTexture));
            final ClickAndDropActor clickAndDropActor = new ClickAndDropActor(stack, mItem, tableImage);
            clickAndDropActor.setWidth(inventoryComponent.backgroundTexture.getRegionWidth());
            clickAndDropActor.setHeight(inventoryComponent.backgroundTexture.getRegionHeight());
            if (clickAndDropSrc) clickAndDrop2.addSource(clickAndDropActor);
            if (clickAndDropDst) clickAndDrop2.addDestination(clickAndDropActor);
            stage.addActor(clickAndDropActor);
            stage.addActor(tableImage);
            tableImages.add(tableImage);
        }
        return tableImages;
    }

    public Table createItemSlotToDisplay(int[] stack, InventoryComponent inventoryComponent) {
        Image image = new Image();
        Label label = new Label(null, skin);
        if (mItem.has(stack[0])) {
            image.setDrawable(new TextureRegionDrawable(mItem.get(stack[0]).itemRegisterEntry.getTextureRegion()));
            label.setText(Integer.toString(InventoryManager.tailleStack(stack)));
        }
        Table table = new Table();
        table.add(image);
        table.addActor(label);
        table.setBackground(new TextureRegionDrawable(inventoryComponent.backgroundTexture));
        label.setFontScale(0.5f);
        label.moveBy(0, inventoryComponent.backgroundTexture.getRegionHeight() - 7);
        return table;
    }
}
