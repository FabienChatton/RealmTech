package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.PlayerComponent;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class PlayerInentoryDisplayManager extends Manager {
    @Wire(name = "context")
    private RealmTech context;
    private Window playerInventoryWindow;

    public void togglePlayerInventoryWindow() {
        if (playerInventoryWindow == null) {
            ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);
            int playerId = world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG);
            InventoryComponent inventoryComponent = mInventory.get(playerId);
            playerInventoryWindow = new Window("Inventaire", context.getSkin());
            playerInventoryWindow.setBounds(context.getUiStage().getWidth() / 2 - 150, context.getUiStage().getHeight() / 2 - 150, 300, 300);
            playerInventoryWindow.add(new Label("" + inventoryComponent.inventory.size(),context.getSkin()));
            context.getUiStage().addActor(playerInventoryWindow);
        } else {
            context.getUiStage().getActors().removeValue(playerInventoryWindow,true);
            playerInventoryWindow = null;
        }
    }
}
