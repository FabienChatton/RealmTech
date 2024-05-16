package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.component.MainPlayerComponent;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.component.LifeComponent;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

@All(MainPlayerComponent.class)
public class ItemBarSystem extends IteratingSystem {
    private Stage itemBarStage;
    private Table itemBarTable;
    private Table itemBar;
    private Table heartBar;
    private byte slotSelected;
    @Wire(name = "context")
    private RealmTech context;
    @Wire
    private SystemsAdminClient systemsAdminClient;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;
    private ComponentMapper<LifeComponent> mLife;

    private int lastHeartNumber = -1;

    @Override
    protected void process(int entityId) {
        displayPlayerItemBar(entityId);
        displayReactiveHeart(entityId);
        itemBarStage.draw();
    }

    @Override
    protected void initialize() {
        itemBarStage = new Stage(context.getUiStage().getViewport(), context.getUiStage().getBatch());
        itemBarTable = new Table();
        itemBarTable.setFillParent(true);
        itemBarStage.addActor(itemBarTable);
        itemBar = new Table();
        heartBar = new Table();

        itemBarTable.add().expandY().bottom().row();
        itemBarTable.add(heartBar).left().row();
        itemBarTable.add(itemBar).row();
        slotSelected = 0;
    }

    public void displayPlayerItemBar(int playerId) {
        itemBar.clear();
        int inventorySize = InventoryComponent.DEFAULT_NUMBER_OF_ROW * InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW;
        int chestInventoryId = systemsAdminClient.getInventoryManager().getChestInventoryId(playerId);
        InventoryComponent chestInventory = mInventory.get(chestInventoryId);
        for (byte j = 0, i = (byte) (inventorySize - InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW); i < inventorySize; i++, j++) {
            Table stackImage = systemsAdminClient.getPlayerInventorySystem().createItemSlotToDisplay(chestInventory.inventory[i], chestInventoryId);
            if (j == slotSelected) {
                final Image selectedSlot = new Image(new TextureRegionDrawable(context.getTextureAtlas().findRegion("inventory-01")));
                selectedSlot.setScale(1.2f);
                selectedSlot.moveBy(-2, -2);
                stackImage.addActor(selectedSlot);
                itemBar.add(stackImage).size(35);
            } else {
                itemBar.add(stackImage);
            }
        }
    }

    public void displayReactiveHeart(int playerId) {
        LifeComponent lifeComponent = mLife.get(playerId);
        if (lifeComponent == null) {
            return;
        }
        int heart = lifeComponent.getHeart();

        if (heart != lastHeartNumber || lastHeartNumber == -1) {
            lastHeartNumber = heart;
            heartBar.clear();
            if (heart > 0) {
                for (int i = 0; i < heart; i++) {
                    heartBar.add(new Image(context.getTextureAtlas().findRegion("heart-02"))).size(12f).padBottom(5f).fillX().left();
                }
            }
        }
    }


    @Override
    protected void dispose() {
        itemBarStage.dispose();
    }

    public void setSlotSelected(byte newSlotDesired) {
        int player = systemsAdminClient.getPlayerManagerClient().getMainPlayer();
        byte newSlot = newSlotDesired;
        int numberOfSlotParRow = systemsAdminClient.getInventoryManager().getChestInventory(player).numberOfSlotParRow;
        if (newSlotDesired >= numberOfSlotParRow) {
            newSlot = 0;
        } else if (newSlotDesired < 0) {
            newSlot = (byte) (numberOfSlotParRow - 1);
        }
        this.slotSelected = newSlot;
    }

    public void slotSelectedUp() {
        setSlotSelected((byte) (this.slotSelected + 1));
    }

    public void slotSelectedDown() {
        setSlotSelected((byte) (this.slotSelected - 1));
    }

    public int[][] getItemBarItems() {
        int player = systemsAdminClient.getPlayerManagerClient().getMainPlayer();
        int[][] inventory = systemsAdminClient.getInventoryManager().getChestInventory(player).inventory;
        int inventorySize = InventoryComponent.DEFAULT_NUMBER_OF_ROW * InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW;
        int[][] ret = new int[InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW][InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW];
        for (byte k = 0, i = (byte) (inventorySize - InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW); i < inventorySize; i++, k++) {
            ret[k] = inventory[i];
        }
        return ret;
    }

    public int getSelectItem() {
        return getSelectStack()[0];
    }

    public int getSelectItemOrNoting() {
        int selectItem = getSelectItem();
        return mItem.has(selectItem) ? selectItem : -1;
    }

    public int[] getSelectStack() {
        return getItemBarItems()[slotSelected];
    }
}
