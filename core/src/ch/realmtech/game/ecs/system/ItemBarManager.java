package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.PlayerComponent;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class ItemBarManager extends BaseSystem {
    private final static String TAG = ItemBarManager.class.getSimpleName();
    private Stage itemBarStage;
    private Table itemBarTable;
    private Table itemBar;
    private byte slotSelected;
    @Wire(name = "context")
    private RealmTech context;
    private ComponentMapper<InventoryComponent> mInventory;

    @Override
    protected void processSystem() {
        world.getSystem(ItemBarManager.class).displayPlayerItemBar(); // mettre une fois en dehors d'une boucle
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
        int player = world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG);
        Array<Table> inventoryTableToDisplay = world.getSystem(PlayerInventoryManager.class).getCellsToDisplay(player);
        int inventorySize = InventoryComponent.DEFAULT_NUMBER_OF_ROW * InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW;
        for (byte k = 0, i = (byte) (inventorySize - InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW); i < inventorySize; i++, k++) {
            Table table = inventoryTableToDisplay.get(i);
            if (k == slotSelected) {
                table.addActor(new Image(new TextureRegionDrawable(context.getTextureAtlas().findRegion("inventory-01"))));
                if (getSelectItem() != 0) {
                    table.getChildren().reverse();
                }
                itemBar.add(table).padLeft(1f);
            } else {
                itemBar.add(table).padLeft(1f);
            }
        }
    }


    @Override
    protected void dispose() {
        itemBarStage.dispose();
    }

    public void setSlotSelected(byte newSlotDesired) {
        int player = world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG);
        byte newSlot = newSlotDesired;
        int numberOfSlotParRow = mInventory.get(player).numberOfSlotParRow;
        if (newSlotDesired >= numberOfSlotParRow) {
            newSlot = 0;
        } else if (newSlotDesired < 0) {
            newSlot = (byte) (numberOfSlotParRow - 1);
        }
        this.slotSelected = newSlot;
        Gdx.app.debug(TAG, "le slot " + this.slotSelected  + " est selectionne");
    }

    public void slotSelectedUp() {
        setSlotSelected((byte) (this.slotSelected + 1));
    }

    public void slotSelectedDown() {
        setSlotSelected((byte) (this.slotSelected - 1));
    }

    public int[][] getItemBarItems() {
        int player = world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG);
        int[][] inventory = mInventory.get(player).inventory;
        int inventorySize = InventoryComponent.DEFAULT_NUMBER_OF_ROW * InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW;
        int[][] ret = new int[InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW][InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW];
        for (byte k = 0, i = (byte) (inventorySize - InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW); i < inventorySize; i++, k++) {
            ret[k] = inventory[i];
        }
        return ret;
    }

    public int getSelectItem() {
        return getItemBarItems()[slotSelected][0];
    }
}
