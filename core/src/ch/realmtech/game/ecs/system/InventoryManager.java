package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.TextureComponent;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;

public class InventoryManager extends Manager {
    @Wire(name = "context")
    private RealmTech context;

    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<TextureComponent> mTexture;
    public void addItemToPlayerInventory(int itemId, int playerId) {
        InventoryComponent inventoryComponent = mInventory.create(playerId);
        for (int slotId = 0; slotId < inventoryComponent.inventory.length; slotId++) {
            int iCellId = inventoryComponent.inventory[slotId][0];
            if (iCellId == 0) {
                inventoryComponent.inventory[slotId][0] = itemId;
                break;
            }
        }
//
//        if (inventoryComponent.inventory.size() < inventoryComponent.maxSize) {
//            world.edit(itemId).remove(PositionComponent.class);
//            inventoryComponent.inventory.add(itemId);
//        }
//        world.getSystem(InventoryPlayerDisplaySystem.class).refreshInventoryWindows();
//        world.getSystem(SoundManager.class).playItemPickUp();
    }

    public void dropItemFromPlayerInventory(int itemId, int playerId, float worldPositionX, float worldPositionY) {
//        InventoryComponent inventoryComponent = mInventory.create(playerId);
//        inventoryComponent.inventory.removeValue(itemId);
//        world.getSystem(ItemManager.class).setItemTexturePositionAndPhysicBody(
//                itemId,
//                mTexture.create(itemId).texture,
//                worldPositionX,
//                worldPositionY
//        );
//        world.getSystem(SoundManager.class).playItemDrop();
    }
    public IntBag getInventory(int playerId) {
        IntBag ret = null;
//        InventoryComponent inventoryComponent = mInventory.get(playerId);
//        if (inventoryComponent != null) {
//            ret = inventoryComponent.inventory;
//        }
        return ret;
    }
}
