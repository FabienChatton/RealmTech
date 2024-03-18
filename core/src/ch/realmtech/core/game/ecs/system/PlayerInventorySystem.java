package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.clickAndDrop.ClickAndDrop2;
import ch.realmtech.core.game.clickAndDrop.ClickAndDropActor;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.core.game.ecs.plugin.strategy.InGameSystemOnInventoryOpen;
import ch.realmtech.core.game.ecs.plugin.strategy.SystemEnableOnInventoryOpen;
import ch.realmtech.core.input.InputMapper;
import ch.realmtech.core.shader.BlurShader;
import ch.realmtech.core.shader.GrayShader;
import ch.realmtech.core.shader.Shaders;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.forclient.SystemsAdminClientForClient;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.inventory.AddAndDisplayInventoryArgs;
import ch.realmtech.server.inventory.DisplayInventoryArgs;
import ch.realmtech.server.newMod.options.client.InventoryBlurOptionEntry;
import ch.realmtech.server.newRegistry.NewItemEntry;
import ch.realmtech.server.newRegistry.RegistryUtils;
import ch.realmtech.server.packet.serverPacket.SubscribeToEntityPacket;
import ch.realmtech.server.packet.serverPacket.UnSubscribeToEntityPacket;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
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
import java.util.List;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static ch.realmtech.server.inventory.DisplayInventoryArgs.builder;

public class PlayerInventorySystem extends BaseSystem {
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<ItemStoredComponent> mStoredItem;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<InventoryUiComponent> mInventoryUi;
    private ComponentMapper<ItemResultCraftComponent> mItemResultCraft;
    private ComponentMapper<CraftingTableComponent> mCraftingTable;
    private Stage inventoryStage;
    private Window inventoryWindow;
    private Window overWindow;
    private Label overLabel;
    private AddAndDisplayInventoryArgs currentInventoryArgs;
    private UUID[] currentInventoriesToClearItemOnClose;
    private Runnable currentOnInventoryClose;
    private ArrayList<ClickAndDropActor> curentClickAndDropActors;
    @Wire(name = "context")
    private RealmTech context;
    @Wire
    private SystemsAdminClient systemsAdminClient;
    @Wire(name = "uiStage")
    private Stage uiStage;
    @Wire(name = "inGameSystemOnInventoryOpen")
    private InGameSystemOnInventoryOpen inGameSystemOnInventoryOpen;
    @Wire(name = "inGameSystemOnInventoryOpenEnable")
    private SystemEnableOnInventoryOpen inGameSystemOnInventoryOpenEnable;
    private ClickAndDrop2 clickAndDrop2;
    private Shaders blurShader;
    private Shaders grayShader;
    private Boolean inventoryBlurOption;

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
                    overWindow.getTitleLabel().setText(itemComponent.itemRegisterEntry.getName());
                    overWindow.setBounds(screenMouseOver.x + 16, screenMouseOver.y - actor.getHeight(), 300, 70);
                    overLabel.setText(itemComponent.itemRegisterEntry.getId());
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
        inventoryBlurOption = RegistryUtils.findEntryOrThrow(context.getRootRegistry(), InventoryBlurOptionEntry.class).getValue();
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
            inGameSystemOnInventoryOpen.onInventoryClose(world);
            inGameSystemOnInventoryOpenEnable.onInventoryClose(world);
            if (currentOnInventoryClose != null) {
                currentOnInventoryClose.run();
                currentOnInventoryClose = null;
            }

            deleteInventoriesToClearItemsOnClose();
            currentInventoriesToClearItemOnClose = null;

            return true;
        } else {
            return false;
        }
    }

    public boolean openPlayerInventory(Supplier<AddAndDisplayInventoryArgs> openPlayerInventoryFunction) {
        if (!isEnabled()) {
            super.setEnabled(true);
            InputMapper.reset();
            clearDisplayInventory();
            currentInventoryArgs = openPlayerInventoryFunction.get();
            currentInventoriesToClearItemOnClose = currentInventoryArgs.inventoriesToDeleteItemsOnClose();
            currentOnInventoryClose = currentInventoryArgs.onInventoryClose();
            refreshInventory(currentInventoryArgs);
            if (inventoryBlurOption) {
                context.getGameStage().getBatch().setShader(grayShader.shaderProgram);
            }
            context.getSoundManager().playOpenInventory();
            inGameSystemOnInventoryOpen.onInventoryOpen(world);
            List<InputProcessor> inputProcessors = inGameSystemOnInventoryOpenEnable.onInventoryOpen(world);
            InputMultiplexer inputMultiplexer = new InputMultiplexer(inventoryStage);
            for (InputProcessor inputProcessor : inputProcessors) {
                inputMultiplexer.addProcessor(inputProcessor);
            }
            Gdx.input.setInputProcessor(inputMultiplexer);
            return true;
        } else {
            return false;
        }
    }

    public Supplier<AddAndDisplayInventoryArgs> getDisplayInventoryPlayer() {
        return () -> {
            final Table playerInventory = new Table(context.getSkin());
            final Table craftingInventory = new Table(context.getSkin());
            final Table craftingResultInventory = new Table(context.getSkin());

            Consumer<Window> addTable = window -> {
                window.add(craftingInventory).padBottom(10f).right();
                window.add(craftingResultInventory).padBottom(10f).row();
                window.add(playerInventory);
            };

            int playerId = context.getSystem(PlayerManagerClient.class).getMainPlayer();

            UUID playerInventoryUuid = systemsAdminClient.uuidEntityManager.getEntityUuid(systemsAdminClient.inventoryManager.getChestInventoryId(playerId));
            UUID playerCraftingInventoryUuid = systemsAdminClient.uuidEntityManager.getEntityUuid(mCraftingTable.get(systemsAdminClient.getPlayerManagerClient().getMainPlayer()).craftingInventory);
            UUID playerCraftingResultUuid = systemsAdminClient.uuidEntityManager.getEntityUuid(mCraftingTable.get(systemsAdminClient.getPlayerManagerClient().getMainPlayer()).craftingResultInventory);

            // subscribe
            context.sendRequest(new SubscribeToEntityPacket(playerCraftingInventoryUuid));
            return new AddAndDisplayInventoryArgs(addTable, new DisplayInventoryArgs[]{
                    builder(playerInventoryUuid, playerInventory)
                            .build(),
                    builder(playerCraftingInventoryUuid, craftingInventory)
                            .build(),
                    builder(playerCraftingResultUuid, craftingResultInventory)
                            .notClickAndDropDst()
                            .build()
            }, new UUID[]{
//                    playerInventoryUuid,
//                    playerCraftingInventoryUuid,
//                    playerCraftingResultUuid
            }, () -> {
                context.sendRequest(new UnSubscribeToEntityPacket(playerCraftingResultUuid));
            });
        };
    }

    public void refreshInventory(AddAndDisplayInventoryArgs displayInventoryArgs) {
        deleteInventoriesToClearItemsOnClose();
        clearDisplayInventory();
        displayAddTable(displayInventoryArgs.addTable());
        displayInventory(displayInventoryArgs.args());
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

    public void displayInventory(DisplayInventoryArgs displayInventoryArgs) {
        int inventoryId = systemsAdminClient.uuidEntityManager.getEntityId(displayInventoryArgs.inventoryUuid());
        InventoryComponent inventoryComponent = mInventory.get(inventoryId);
        Array<Table> tableImages = createItemSlotsToDisplay(displayInventoryArgs.inventoryUuid(), inventoryStage, displayInventoryArgs.isClickAndDropSrc(), displayInventoryArgs.isClickAndDropDst(), displayInventoryArgs.getDstRequirePredicate());
        for (int i = 0; i < tableImages.size; i++) {
            if (i % inventoryComponent.numberOfSlotParRow == 0) {
                displayInventoryArgs.inventoryTable().row();
            }
            displayInventoryArgs.inventoryTable().add(tableImages.get(i));
        }
    }

    public Array<Table> createItemSlotsToDisplay(UUID inventoryUuid, Stage stage, boolean clickAndDropSrc, boolean clickAndDropDst, BiPredicate<SystemsAdminClientForClient, NewItemEntry> dstRequirePredicate) {
        final Array<Table> tableImages = new Array<>();
        int inventoryOriginalId = systemsAdminClient.uuidEntityManager.getEntityId(inventoryUuid);
        InventoryComponent inventoryComponent = mInventory.get(inventoryOriginalId);
        InventoryUiComponent inventoryUiComponent = mInventoryUi.get(inventoryOriginalId);
        int[][] inventory = inventoryComponent.inventory;
        for (int i = 0; i < inventory.length; i++) {
            int finalI = i;
            Supplier<int[]> getStack = () -> {
                int inventoryId = systemsAdminClient.uuidEntityManager.getEntityId(inventoryUuid);
                if (inventoryId == -1) {
                    System.out.println(inventoryUuid);
                    return new int[1];
                }
                return mInventory.get(inventoryId).inventory[finalI];
            };

            Table tableImage = new Table();
            ClickAndDropActor clickAndDropActor = new ClickAndDropActor(context, inventoryUuid, getStack, tableImage, dstRequirePredicate);
            curentClickAndDropActors.add(clickAndDropActor);
            TextureRegion backGroundTextureRegion = context.getTextureAtlas().findRegion(inventoryUiComponent.backgroundTexture);
            tableImage.setBackground(new TextureRegionDrawable(backGroundTextureRegion));
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

    public Table createItemSlotToDisplay(int[] stack, int inventoryId) {
        Image image = new Image();
        Label nombreItem = new Label(null, context.getSkin());
        InventoryUiComponent inventoryUiComponent = mInventoryUi.get(inventoryId);
        final TextureRegion backGroundTextureRegion = context.getTextureAtlas().findRegion(inventoryUiComponent.backgroundTexture);
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

    private void deleteInventoriesToClearItemsOnClose() {
        for (UUID inventoryToClearItem : currentInventoriesToClearItemOnClose) {
            int inventoryId = systemsAdminClient.inventoryManager.getInventoryByUUID(inventoryToClearItem);
            if (inventoryId != -1) {
                InventoryComponent inventoryComponent = mInventory.get(inventoryId);
                for (int[] stack : inventoryComponent.inventory) {
                    systemsAdminClient.inventoryManager.deleteStack(stack);
                }
            }
        }
    }

    public void createClickAndDrop(int playerId) {
        this.clickAndDrop2 = new ClickAndDrop2(context, inventoryStage, world, playerId, systemsAdminClient);
    }
}
