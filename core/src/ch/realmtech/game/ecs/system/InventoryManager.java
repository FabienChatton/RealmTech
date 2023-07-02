package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.TextureComponent;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;

import java.util.Arrays;

public class InventoryManager extends Manager {
    @Wire(name = "context")
    private RealmTech context;

    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<TextureComponent> mTexture;
    public void addItemToInventory(int itemId, int entityId) {
        InventoryComponent inventoryComponent = mInventory.get(entityId);
        for (int slotId = 0; slotId < inventoryComponent.inventory.length; slotId++) {
            int iCellId = inventoryComponent.inventory[slotId][0];
            if (iCellId == 0) {
                inventoryComponent.inventory[slotId][0] = itemId;
                break;
            }
        }
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

    public int[][] getInventory(int entityId) {
        return mInventory.get(entityId).inventory;
    }

    public void clearInventory(int[][] inventory) {
        for (int[] ints : inventory) {
            Arrays.fill(ints, 0);
        }
    }
}
