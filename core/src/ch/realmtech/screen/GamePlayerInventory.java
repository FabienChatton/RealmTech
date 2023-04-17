package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.ecs.component.StoredItemComponent;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class GamePlayerInventory extends AbstractScreen {
    private final Table playerInventoryTable;
    private final Table playerDisplayItemTable;

    public GamePlayerInventory(RealmTech context) {
        super(context);
        playerInventoryTable = new Table(skin);
        playerInventoryTable.setFillParent(true);

        Label playerInventory = new Label("Player Inventory", skin);
        playerDisplayItemTable = new Table(skin);
        playerInventoryTable.add(playerInventory);
        playerInventoryTable.row();
        playerInventoryTable.add(playerDisplayItemTable).fill().expand();
        uiTable.add(playerInventoryTable).expand().fill().center();
    }


    public void setInventoryToDisplay(int playerId) {
        playerDisplayItemTable.clear();
        ComponentMapper<ItemComponent> mItem = context.getEcsEngine().getEcsWorld().getMapper(ItemComponent.class);
        ComponentMapper<StoredItemComponent> mStoredItem = context.getEcsEngine().getEcsWorld().getMapper(StoredItemComponent.class);

        int[][] inventory = context.getEcsEngine().getEcsWorld().getMapper(InventoryComponent.class).get(playerId).inventory;
        for (int cellId = 0; cellId < inventory.length; cellId++) {
            int itemId = inventory[cellId][0];
            final Image itemDisplayImage;
            if (itemId != 0) {
                itemDisplayImage = new Image(mItem.get(itemId).itemRegisterEntry.getTextureRegion());
            } else {
                itemDisplayImage = new Image(context.getTextureAtlas().findRegion("grass-01"));
            }
            if (cellId % InventoryComponent.NUMBER_OF_SLOT_PAR_ROW == 0) {
                playerDisplayItemTable.row();
            }
            playerDisplayItemTable.add(itemDisplayImage);
        }
    }

    @Override
    public void show() {
        super.show();
        uiStage.setDebugAll(true);
        setInventoryToDisplay(context.getPlayerId());
    }

    @Override
    public void hide() {
        super.hide();
        uiStage.setDebugAll(false);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            context.setScreen(ScreenType.GAME_SCREEN);
        }
    }
}
