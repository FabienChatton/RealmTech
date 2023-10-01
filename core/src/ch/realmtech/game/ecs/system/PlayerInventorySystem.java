package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.clickAndDrop.ClickAndDrop2;
import ch.realmtech.game.clickAndDrop.ClickAndDropActor;
import ch.realmtech.game.inventory.AddAndDisplayInventoryArgs;
import ch.realmtech.game.inventory.DisplayInventoryArgs;
import ch.realmtech.input.InputMapper;
import ch.realmtech.shader.BlurShader;
import ch.realmtech.shader.GrayShader;
import ch.realmtech.shader.Shaders;
import ch.realmtech.strategy.InGameSystemOnInventoryOpen;
import ch.realmtechServer.ecs.component.*;
import ch.realmtechServer.ecs.system.InventoryManager;
import ch.realmtechServer.mod.RealmTechCoreMod;
import ch.realmtechServer.registery.ItemRegisterEntry;
import ch.realmtechServer.registery.RegistryEntry;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public class PlayerInventorySystem extends BaseSystem {
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<StoredItemComponent> mStoredItem;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<ItemResultCraftComponent> mItemResultCraft;
    private ComponentMapper<CraftingTableComponent> mCraftingTable;
    private Stage inventoryStage;
    private Window inventoryWindow;
    private Window overWindow;
    private Label overLabel;
    private AddAndDisplayInventoryArgs currentInventoryArgs;
    private ArrayList<ClickAndDropActor> curentClickAndDropActors;
    @Wire(name = "context")
    private RealmTech context;
    @Wire(name = "uiStage")
    private Stage uiStage;
    @Wire(name = "inGameSystemOnInventoryOpen")
    private InGameSystemOnInventoryOpen inGameSystemOnInventoryOpen;
    private ClickAndDrop2 clickAndDrop2;
    private Shaders blurShader;
    private Shaders grayShader;

    @Override
    protected void processSystem() {
        showMouseOverLabel();
        inventoryStage.setDebugAll(uiStage.isDebugAll());
        inventoryStage.act();
        inventoryStage.draw();
    }

    private void showMouseOverLabel() {
        if (!uiStage.getActors().contains(overLabel, true)) {
            uiStage.addActor(overWindow);
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
    }

    /*
    inventoryStage
    inventoryWindow
    inventoryTable
     */
    @Override
    protected void initialize() {
        super.initialize();
        this.inventoryStage = new Stage(uiStage.getViewport(), uiStage.getBatch());
        this.inventoryWindow = new Window("Inventaire", context.getSkin()) {
            @Override
            public void act(float delta) {
                super.act(delta);
                setZIndex(0);
            }
        };

        float with = inventoryStage.getWidth() * 0.5f;
        float height = inventoryStage.getHeight() * 0.5f;
        inventoryWindow.setBounds((inventoryStage.getWidth() - with) / 2, (inventoryStage.getHeight() - height) / 2, with, height);
        inventoryStage.addActor(inventoryWindow);
        setEnabled(false);
        this.clickAndDrop2 = new ClickAndDrop2(context, inventoryStage, world);
        blurShader = new BlurShader();
        grayShader = new GrayShader();
        inventoryWindow.setColor(Color.WHITE);
        overWindow = new Window("", context.getSkin());
        overWindow.setTouchable(Touchable.disabled);
        overLabel = new Label(null, context.getSkin());
        overWindow.add(overLabel).expand().left();
        overLabel.setFontScale(0.5f);
        overWindow.setColor(new Color(1, 1, 1, 0.8f));
        curentClickAndDropActors = new ArrayList<>();
    }

    public boolean closePlayerInventory() {
        if (isEnabled()) {
            super.setEnabled(false);
            Gdx.input.setInputProcessor(context.getInputManager());
            context.getGameStage().getBatch().setShader(null);
            context.getSoundManager().playOpenInventory();
            overWindow.remove();
            inGameSystemOnInventoryOpen.activeInGameSystemOnPause(world);
            return true;
        } else {
            return false;
        }
    }

    private boolean openPlayerInventory(Function<RealmTech, AddAndDisplayInventoryArgs> openPlayerInventoryFunction) {
        if (!isEnabled()) {
            super.setEnabled(true);
            InputMapper.reset();
            clearDisplayInventory();
            currentInventoryArgs = openPlayerInventoryFunction.apply(context);
            refreshInventory(currentInventoryArgs);
            if (context.getDataCtrl().option.inventoryBlur.get()) {
                context.getGameStage().getBatch().setShader(grayShader.shaderProgram);
            }
            context.getSoundManager().playOpenInventory();
            inGameSystemOnInventoryOpen.disableInGameSystemOnPause(world);
//            for (Actor actor : inventoryStage.getActors().items) {
//                if (actor instanceof ClickAndDropActor clickAndDropActor) {
//                    int[] stack = clickAndDropActor.getStack();
//                    for (int itemId : stack) {
//                        if (mItem.has(itemId)) {
//                            System.out.println(mItem.get(itemId) + ", itemId:" + itemId);
//                        }
//                    }
//                }
//            }
            return true;
        } else {
            return false;
        }
    }

    public void toggleInventoryWindow(Function<RealmTech, AddAndDisplayInventoryArgs> openPlayerInventoryFunction) {
        if (isEnabled()) {
            closePlayerInventory();
        } else {
            openPlayerInventory(openPlayerInventoryFunction);
        }
    }

    public Function<RealmTech, AddAndDisplayInventoryArgs> getDisplayInventoryPlayer() {
        return (context) -> {
            final Table playerInventory = new Table(context.getSkin());
            final Table craftingInventory = new Table(context.getSkin());
            final Table craftingResultInventory = new Table(context.getSkin());

            Consumer<Window> addTable = window -> {
                window.add(craftingInventory).padBottom(10f).right();
                window.add(craftingResultInventory).padBottom(10f).row();
                window.add(playerInventory);
            };

            return new AddAndDisplayInventoryArgs(addTable, new DisplayInventoryArgs[]{
                    DisplayInventoryArgs.builder(mInventory.get(context.getEcsEngine().getPlayerId()), playerInventory).build(),
                    DisplayInventoryArgs.builder(mInventory.get(mCraftingTable.get(world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG)).craftingInventory), craftingInventory)
                            .build(),
                    DisplayInventoryArgs.builder(mInventory.get(mCraftingTable.get(world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG)).craftingResultInventory), craftingResultInventory)
                            .notClickAndDropDst()
                            .build()
            });
        };
    }

    public void refreshInventory(AddAndDisplayInventoryArgs displayInventoryArgs) {
        displayAddTable(displayInventoryArgs.addTable());
        displayInventory(displayInventoryArgs.args());
        Gdx.input.setInputProcessor(inventoryStage);
    }

    private void clearDisplayInventory() {
        for (Actor actor : inventoryWindow.getChildren().items) {
            if (actor != null) {
                actor.clear();
            }
        }
        curentClickAndDropActors.forEach(Actor::remove);
        inventoryWindow.clear();
        clickAndDrop2.clearActor();
    }

    private void displayAddTable(Consumer<Window> addTable) {
        addTable.accept(inventoryWindow);
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
            curentClickAndDropActors.add(clickAndDropActor);
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
        Label nombreItem = new Label(null, context.getSkin());
        final TextureRegion backGroundTextureRegion = context.getTextureAtlas().findRegion(inventoryComponent.backgroundTexture);
        if (mItem.has(stack[0])) {
            image.setDrawable(new TextureRegionDrawable(mItem.get(stack[0]).itemRegisterEntry.getTextureRegion(context.getTextureAtlas())));
            nombreItem.setText(Integer.toString(InventoryManager.tailleStack(stack)));
        }
        Table table = new Table();
        table.add(image);
        table.addActor(nombreItem);
        table.setBackground(new TextureRegionDrawable(backGroundTextureRegion));
        nombreItem.setFontScale(0.5f);
        nombreItem.moveBy(0, backGroundTextureRegion.getRegionHeight() - 7);
        return table;
    }
}
