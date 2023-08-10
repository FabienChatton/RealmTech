package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.clickAndDrop.ClickAndDrop2;
import ch.realmtech.game.clickAndDrop.ClickAndDropActor;
import ch.realmtech.game.craft.CraftResult;
import ch.realmtech.game.craft.DisplayInventoryArgs;
import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.mod.RealmTechCoreMod;
import ch.realmtech.game.registery.CraftingRecipeEntry;
import ch.realmtech.game.registery.ItemRegisterEntry;
import ch.realmtech.game.registery.RegistryEntry;
import ch.realmtech.input.InputMapper;
import ch.realmtech.shader.BlurShader;
import ch.realmtech.shader.GrayShader;
import ch.realmtech.shader.Shaders;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class PlayerInventorySystem extends BaseSystem {
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<StoredItemComponent> mStoredItem;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<ItemResultCraftComponent> mItemResultCraft;
    private ComponentMapper<CraftingTableComponent> mCraftingTable;
    private Stage inventoryStage;
    private Window inventoryWindow;
    private Table inventoryPlayerTable;
    private Table inventoryCraftingTable;
    private Table inventoryCraftResultTable;
    private Window overWindow;
    private Label overLabel;
    private DisplayInventoryArgs[] currentInventoryArgs;
    @Wire(name = "context")
    private RealmTech context;
    private Skin skin;
    private ClickAndDrop2 clickAndDrop2;
    private Shaders blurShader;
    private Shaders grayShader;

    @Override
    protected void processSystem() {
        if (!context.getUiStage().getActors().contains(overLabel, true)) {
            context.getUiStage().addActor(overWindow);
        }
        Vector2 screenMouseOver = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        inventoryStage.screenToStageCoordinates(screenMouseOver);
        boolean trouve = false;
        for (ClickAndDropActor actor : clickAndDrop2.getActors()) {
            Rectangle actorRectangle = new Rectangle(actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight());
            if (actorRectangle.contains(screenMouseOver)) {
                trouve = true;
                ItemComponent itemComponent = mItem.get(actor.getStack()[0]);
                if (itemComponent != null) {
                    RegistryEntry<ItemRegisterEntry> registryEntry = itemComponent.itemRegisterEntry.findRegistryEntry(RealmTechCoreMod.ITEMS)
                            .or(() -> RealmTechCoreMod.NO_ITEM.findRegistryEntry(RealmTechCoreMod.ITEMS))
                            .orElseThrow();
                    overWindow.getTitleLabel().setText(registryEntry.getName());
                    overWindow.setBounds(screenMouseOver.x + 16, screenMouseOver.y - actor.getHeight(), 300, 70);
                    overLabel.setText(registryEntry.getID() + "\n" + registryEntry.getHashID());
                } else {
                    overWindow.remove();
                }
            }
        }
        if (!trouve) {
            overWindow.remove();
        }
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
        this.skin = context.getSkin();
        this.inventoryCraftingTable = new Table(context.getSkin());
        this.inventoryCraftResultTable = new Table(context.getSkin());
        this.inventoryPlayerTable = new Table(context.getSkin());
        inventoryWindow.add(inventoryCraftingTable).padBottom(10f).right();
        inventoryWindow.add(inventoryCraftResultTable).padBottom(10f).row();
        inventoryWindow.add(inventoryPlayerTable);
        float with = inventoryStage.getWidth() * 0.5f;
        float height = inventoryStage.getHeight() * 0.5f;
        inventoryWindow.setBounds((inventoryStage.getWidth() - with) / 2, (inventoryStage.getHeight() - height) / 2, with, height);
        inventoryStage.addActor(inventoryWindow);
        setEnabled(false);
        this.clickAndDrop2 = new ClickAndDrop2(context, inventoryStage, world);
        blurShader = new BlurShader();
        grayShader = new GrayShader();
        inventoryWindow.setColor(Color.WHITE);
        overWindow = new Window("", skin);
        overWindow.setTouchable(Touchable.disabled);
        overLabel = new Label(null, skin);
        overWindow.add(overLabel).expand().left();
        overLabel.setFontScale(0.5f);
        overWindow.setColor(new Color(1, 1, 1, 0.8f));
    }

    public boolean closePlayerInventory() {
        if (isEnabled()) {
            super.setEnabled(false);
            Gdx.input.setInputProcessor(context.getInputManager());
            context.getGameStage().getBatch().setShader(null);
            context.getSoundManager().playOpenInventory();
            overWindow.remove();
            return true;
        } else {
            return false;
        }
    }

    public boolean openPlayerInventory(DisplayInventoryArgs[] displayInventoryArgs) {
        if (!isEnabled()) {
            super.setEnabled(true);
            InputMapper.reset();
            currentInventoryArgs = displayInventoryArgs;
            refreshInventory(currentInventoryArgs);
            if (context.getRealmTechDataCtrl().option.inventoryBlur.get()) {
                context.getGameStage().getBatch().setShader(grayShader.shaderProgram);
            }
            context.getSoundManager().playOpenInventory();
            return true;
        } else {
            return false;
        }
    }

    public void toggleInventoryWindow(DisplayInventoryArgs[] displayInventoryArgs) {
        if (isEnabled()) {
            closePlayerInventory();
        } else {
            openPlayerInventory(displayInventoryArgs);
        }
    }

    public DisplayInventoryArgs[] getDisplayInventoryPlayerArgs() {
        return new DisplayInventoryArgs[]{
                new DisplayInventoryArgs(mInventory.get(context.getEcsEngine().getPlayerId()), inventoryPlayerTable, true, true, false, false),
                new DisplayInventoryArgs(mInventory.get(mCraftingTable.get(world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG)).craftingInventory), inventoryCraftingTable, true, true, true, false),
                new DisplayInventoryArgs(mInventory.get(mCraftingTable.get(world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG)).craftingResultInventory), inventoryCraftResultTable, true, false, false, true)
        };
    }

    public DisplayInventoryArgs[] getDisplayCraftingInventoryArgs(InventoryComponent playerInventory, InventoryComponent inventoryCraft, InventoryComponent inventoryResult) {
        return new DisplayInventoryArgs[]{
                new DisplayInventoryArgs(playerInventory, inventoryPlayerTable, true, true, false, false),
                new DisplayInventoryArgs(inventoryCraft, inventoryCraftingTable, true, true, true, false),
                new DisplayInventoryArgs(inventoryResult, inventoryCraftResultTable, true, false, false, true)
        };
    }

    public DisplayInventoryArgs[] getDisplayInventory(InventoryComponent inventoryComponent) {
        return new DisplayInventoryArgs[]{
                new DisplayInventoryArgs(mInventory.get(context.getEcsEngine().getPlayerId()), inventoryPlayerTable, true, true, false, false),
                new DisplayInventoryArgs(inventoryComponent, inventoryCraftingTable, true, true, false, false)
        };
    }

    public void refreshInventory(DisplayInventoryArgs[] displayInventoryArgs) {
        clearDisplayInventory();
        displayInventory(displayInventoryArgs);
        Gdx.input.setInputProcessor(inventoryStage);
    }

    public boolean nouveauCraftDisponible(CraftResult craftResult, CraftingRecipeEntry craftingRecipeEntry, InventoryComponent resultInventory) {
        final int[][] inventory = resultInventory.inventory;
        if (!mItem.has(inventory[0][0])) {
            if (mItem.get(inventory[0][0]) != null && mItem.get(inventory[0][0]).itemRegisterEntry == craftResult.itemRegisterEntry()) {
                //return false; // il y a déjà le craft dans le résultat
            }
            for (int i = 0; i < craftResult.nombre(); i++) {
                int itemResultId = world.getSystem(ItemManager.class).newItemInventory(craftResult.itemRegisterEntry());
                final ItemResultCraftComponent itemResultCraftComponent = world.edit(itemResultId).create(ItemResultCraftComponent.class);
                itemResultCraftComponent.craftingRecipeEntry = craftingRecipeEntry;
                world.getSystem(InventoryManager.class).addItemToInventory(itemResultId, getCurrentCraftResultInventory());
            }
            if (isEnabled()) {
                refreshInventory(currentInventoryArgs);
            }
        }
        return true;
    }

    public void aucunCraftDisponible(InventoryComponent inventoryComponent) {
        int[][] inventory = inventoryComponent.inventory;
        if (inventory[0][0] != 0) {
            world.getSystem(InventoryManager.class).removeInventory(inventory);
        }
    }

    private void clearDisplayInventory() {
        inventoryPlayerTable.clear();
        inventoryCraftingTable.clear();
        inventoryCraftResultTable.clear();
        clickAndDrop2.clearActor();
    }

    private void displayInventory(DisplayInventoryArgs[] displayInventoryPlayerArgs) {
        for (DisplayInventoryArgs displayInventoryPlayerArg : displayInventoryPlayerArgs) {
            displayInventory(displayInventoryPlayerArg);
        }
    }

    /**
     * Ajout les cases de l'inventaire dans le tableau passé en second paramètre.
     *
     * @param displayInventoryArgs
     */
    public void displayInventory(DisplayInventoryArgs displayInventoryArgs) {
        Array<Table> tableImages = createItemSlotsToDisplay(displayInventoryArgs.inventoryComponent(), inventoryStage, displayInventoryArgs.clickAndDropSrc(), displayInventoryArgs.clickAndDropDst());
        for (int i = 0; i < tableImages.size; i++) {
            if (i % displayInventoryArgs.inventoryComponent().numberOfSlotParRow == 0) {
                displayInventoryArgs.inventoryTable().row();
            }
            displayInventoryArgs.inventoryTable().add(tableImages.get(i));
        }
    }

    public Array<Table> createItemSlotsToDisplay(InventoryComponent inventoryComponent, Stage stage, boolean clickAndDropSrc, boolean clickAndDropDst) {
        final Array<Table> tableImages = new Array<>();
        int[][] inventory = inventoryComponent.inventory;
        for (int[] stack : inventory) {
            final Table tableImage = new Table();
            final TextureRegion backGroundTextureRegion = context.getTextureAtlas().findRegion(inventoryComponent.backgroundTexture);
            tableImage.setBackground(new TextureRegionDrawable(backGroundTextureRegion));
            final ClickAndDropActor clickAndDropActor = new ClickAndDropActor(context, stack, mItem, tableImage);
            clickAndDropActor.setWidth(backGroundTextureRegion.getRegionWidth());
            clickAndDropActor.setHeight(backGroundTextureRegion.getRegionHeight());
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
        final TextureRegion backGroundTextureRegion = context.getTextureAtlas().findRegion(inventoryComponent.backgroundTexture);
        if (mItem.has(stack[0])) {
            image.setDrawable(new TextureRegionDrawable(mItem.get(stack[0]).itemRegisterEntry.getTextureRegion(context)));
            label.setText(Integer.toString(InventoryManager.tailleStack(stack)));
        }
        Table table = new Table();
        table.add(image);
        table.addActor(label);
        table.setBackground(new TextureRegionDrawable(backGroundTextureRegion));
        label.setFontScale(0.5f);
        label.moveBy(0, backGroundTextureRegion.getRegionHeight() - 7);
        return table;
    }

    public InventoryComponent getCurrentCraftResultInventory() throws NoSuchElementException {
        if (currentInventoryArgs == null) {
            throw new NoSuchElementException();
        }
        return Arrays.stream(currentInventoryArgs)
                .filter(DisplayInventoryArgs::isCraftResult)
                .findFirst()
                .orElseThrow()
                .inventoryComponent();
    }

    public InventoryComponent getCurrentCraftingInventory() throws NoSuchElementException {
        if (currentInventoryArgs == null) {
            throw new NoSuchElementException();
        }
        return Arrays.stream(currentInventoryArgs)
                .filter(DisplayInventoryArgs::isCrafting)
                .findFirst()
                .orElseThrow()
                .inventoryComponent();
    }
}
