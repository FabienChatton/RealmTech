package ch.realmtech.server.serialize.inventory;

import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.component.UuidComponent;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.registery.ItemRegisterEntry;
import com.artemis.ComponentMapper;
import com.artemis.World;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.function.Function;

public class InventorySerializerV2 implements InventorySerializer {
    private final static int VERSION = 2;
    @Override
    public byte[] toBytes(InventoryComponent inventoryComponent, World world) {
        ComponentMapper<ItemComponent> mItem = world.getMapper(ItemComponent.class);
        ComponentMapper<UuidComponent> mUuid = world.getMapper(UuidComponent.class);
        ByteBuffer byteBuffer = ByteBuffer.allocate(getTailleBytes(inventoryComponent));
        byteBuffer.putInt(VERSION);
        byteBuffer.putInt(inventoryComponent.numberOfRow);
        byteBuffer.putInt(inventoryComponent.numberOfSlotParRow);
        byteBuffer.putInt(InventoryComponent.DEFAULT_STACK_LIMITE);
        byteBuffer.putInt(getNumberNotEmtpySlot(inventoryComponent));
        byteBuffer.putInt(getNumberOfItems(inventoryComponent));

        for (int i = 0; i < inventoryComponent.numberOfRow; i++) {
            for (int j = 0; j < inventoryComponent.numberOfSlotParRow; j++) {
                int index = i * inventoryComponent.numberOfSlotParRow + j;
                // write index if there are items on this slot
                int[] stack = inventoryComponent.inventory[index];
                int itemId = stack[0];
                if (itemId != 0) {
                    byteBuffer.putInt(index);
                    // hash
                    byteBuffer.putInt(ItemRegisterEntry.getHash(mItem.get(itemId).itemRegisterEntry));
                    int numberOfItem = InventoryManager.tailleStack(stack);
                    byteBuffer.putInt(numberOfItem);
                    for (int n = 0; n < numberOfItem; n++) {
                        UUID itemUuid = mUuid.get(itemId).getUuid();
                        ByteBufferHelper.writeUUID(byteBuffer, itemUuid);
                    }
                }
            }
        }
        return byteBuffer.array();
    }

    @Override
    public Function<ItemManager, int[][]> fromBytes(World world, byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        // header
        int version = byteBuffer.getInt();
        int numberOfRow = byteBuffer.getInt();
        int numberOfSlotParRow = byteBuffer.getInt();
        int numberNotEmtpySlot = byteBuffer.getInt();
        int numberOfItems = byteBuffer.getInt();

        ItemRegisterEntry[] itemsRegistry = new ItemRegisterEntry[numberNotEmtpySlot];
        UUID[] itemsUuid = new UUID[numberOfItems];
        byte[] itemsNumber = new byte[numberOfItems];
        int uuidIndex = 0;
        // body
        for (int i = 0; i < numberNotEmtpySlot; i++) {
            int index = byteBuffer.getInt();
            int hash = byteBuffer.getInt();
            itemsRegistry[index] = ItemRegisterEntry.getItemByHash(hash);
            int numberOfItem = byteBuffer.getInt();
            for (int n = 0; n < numberOfItem; n++) {
                UUID uuid = ByteBufferHelper.readUUID(byteBuffer);
                itemsUuid[uuidIndex++] = uuid;
            }
        }
        return null;
    }

    private static int getNumberNotEmtpySlot(InventoryComponent inventoryComponent) {
        int numberNotEmtpySlot = 0;
        for (int i = 0; i < inventoryComponent.numberOfRow; i++) {
            for (int j = 0; j < inventoryComponent.numberOfSlotParRow; j++) {
                int index = i * inventoryComponent.numberOfSlotParRow + j;
                // write index if there are items on this slot
                int[] stack = inventoryComponent.inventory[index];
                int itemId = stack[0];
                if (itemId != 0) {
                    ++numberNotEmtpySlot;
                }
            }
        }
        return numberNotEmtpySlot;
    }

    private static int getNumberOfItems(InventoryComponent inventoryComponent) {
        int numberOfItems = 0;
        for (int i = 0; i < inventoryComponent.numberOfRow; i++) {
            for (int j = 0; j < inventoryComponent.numberOfSlotParRow; j++) {
                int index = i * inventoryComponent.numberOfSlotParRow + j;
                int[] stack = inventoryComponent.inventory[index];
                int itemId = stack[0];
                if (itemId != 0) {
                    numberOfItems += InventoryManager.tailleStack(stack);
                }
            }
        }
        return numberOfItems;
    }

    private int getTailleBytes(InventoryComponent inventoryComponent) {
        int numberOfItems = getNumberNotEmtpySlot(inventoryComponent);
        // version protocole, number of row, number of column, stack size, number of not empty stack, number of items, number of items * (index + hash + uuid)
        return Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES + numberOfItems * (Integer.BYTES + Integer.BYTES + (Long.BYTES * 2));
    }
}
