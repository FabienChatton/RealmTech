package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.plgin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ItemBarManager extends BaseSystem {
    private Stage itemBarStage;
    private Table itemBarTable;
    private Table itemBar;
    private byte slotSelected;
    @Wire(name = "context")
    private RealmTech context;
    @Wire
    private SystemsAdminClient systemsAdminClient;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<ItemComponent> mItem;

    @Override
    protected void processSystem() {
        displayPlayerItemBar();
        itemBarStage.draw();
    }

    @Override
    protected void initialize() {
        itemBarStage = new Stage(context.getUiStage().getViewport(), context.getUiStage().getBatch());
        itemBarTable = new Table();
        itemBarTable.setFillParent(true);
        itemBarStage.addActor(itemBarTable);
        itemBar = new Table();
        itemBarTable.add(itemBar).expandY().bottom();
        slotSelected = 0;
    }

    public void displayPlayerItemBar() {
        itemBar.clear();
        int player = systemsAdminClient.tagManager.getEntityId(PlayerManagerClient.MAIN_PLAYER_TAG);
        int inventorySize = InventoryComponent.DEFAULT_NUMBER_OF_ROW * InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW;
        final InventoryComponent inventoryComponent = mInventory.get(player);
        for (byte j = 0, i = (byte) (inventorySize - InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW); i < inventorySize; i++, j++) {
            Table stackImage = systemsAdminClient.playerInventorySystem.createItemSlotToDisplay(inventoryComponent.inventory[i], inventoryComponent);
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


    @Override
    protected void dispose() {
        itemBarStage.dispose();
    }

    public void setSlotSelected(byte newSlotDesired) {
        int player = systemsAdminClient.tagManager.getEntityId(PlayerManagerClient.MAIN_PLAYER_TAG);
        byte newSlot = newSlotDesired;
        int numberOfSlotParRow = mInventory.get(player).numberOfSlotParRow;
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
        int player = systemsAdminClient.tagManager.getEntityId(PlayerManagerClient.MAIN_PLAYER_TAG);
        int[][] inventory = mInventory.get(player).inventory;
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

    public ItemComponent getSelectItemComponent() {
        return mItem.get(getSelectItem());
    }

    public int[] getSelectStack() {
        return getItemBarItems()[slotSelected];
    }
}
