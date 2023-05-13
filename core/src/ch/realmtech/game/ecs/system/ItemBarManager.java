package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.PlayerComponent;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

public class ItemBarManager extends BaseSystem {
    private Stage itemBarStage;
    private Table itemBarTable;
    private Table itemBar;
    @Wire(name = "context")
    private RealmTech context;

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
    }

    public void displayPlayerItemBar() {
        itemBar.clear();
        Array<Table> inventoryTableToDisplay = world.getSystem(PlayerInventoryManager.class).getInventoryTableToDisplay(world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG));
        for (int i = inventoryTableToDisplay.size - InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW; i < inventoryTableToDisplay.size; i++) {
            Table actor = inventoryTableToDisplay.get(i);
            itemBar.add(actor).padLeft(1f);
        }
    }

    @Override
    protected void dispose() {
        itemBarStage.dispose();
    }
}
