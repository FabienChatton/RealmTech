package ch.realmtech.game.craft;

import ch.realmtech.game.ecs.component.InventoryComponent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public record DisplayInventoryArgs(InventoryComponent inventoryComponent, Table inventoryTable, boolean clickAndDropSrc,
                                   boolean clickAndDropDst, boolean isCrafting, boolean isCraftResult) {

}