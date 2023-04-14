package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.PositionComponent;
import com.artemis.ComponentMapper;
import com.artemis.Manager;

public class InventoryManager extends Manager {
    private ComponentMapper<InventoryComponent> mInventory;

    public void addItemToPlayerInventory(int itemId, int playerId) {
        InventoryComponent inventoryComponent = mInventory.create(playerId);
        if (inventoryComponent.inventory.size() < inventoryComponent.maxSize) {
            world.edit(itemId).remove(PositionComponent.class);
            inventoryComponent.inventory.add(itemId);
        }
    }

    public void dropItemFromPlayerInventory(int itemId, int playerId, float worldPositionX, float worldPositionY) {
        InventoryComponent inventoryComponent = mInventory.create(playerId);
        inventoryComponent.inventory.removeValue(itemId);
        PositionComponent positionComponent = world.edit(itemId).create(PositionComponent.class);
        positionComponent.x = worldPositionX;
        positionComponent.y = worldPositionY;
    }
}
