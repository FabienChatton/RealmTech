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
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Function;

public class InventorySerializerV1 implements InventorySerializer {
    private final static int VERSION = 1;
    @Override
    public byte[] toBytes(InventoryComponent inventoryComponent, World world) {
        ComponentMapper<ItemComponent> mItem = world.getMapper(ItemComponent.class);
        ComponentMapper<UuidComponent> mUuid = world.getMapper(UuidComponent.class);
        // header
        ByteBuffer byteBuffer = ByteBuffer.allocate(getTailleBytes(inventoryComponent));
        byteBuffer.putInt(VERSION);
        byteBuffer.putInt(inventoryComponent.numberOfRow);
        byteBuffer.putInt(inventoryComponent.numberOfSlotParRow);
        // body
        for (int i = 0; i < inventoryComponent.numberOfRow; i++) {
            for (int j = 0; j < inventoryComponent.numberOfSlotParRow; j++) {
                int index = i * inventoryComponent.numberOfSlotParRow + j;
                // si pas d'item dans le slot, met 0
                int itemId = inventoryComponent.inventory[index][0];
                if (itemId == 0) {
                    byteBuffer.putInt(0);
                    byteBuffer.putLong(0);
                    byteBuffer.putLong(0);
                } else {
                    byteBuffer.putInt(ItemRegisterEntry.getHash(mItem.get(itemId).itemRegisterEntry));
                    ByteBufferHelper.writeUUID(byteBuffer, mUuid.get(itemId).getUuid());
                }
                byteBuffer.put((byte) InventoryManager.tailleStack(inventoryComponent.inventory[index]));
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
        ItemRegisterEntry[] itemsRegistry = new ItemRegisterEntry[numberOfRow * numberOfSlotParRow];
        UUID[] itemsUuid = new UUID[numberOfRow * numberOfSlotParRow];
        byte[] itemsNumber = new byte[numberOfRow * numberOfSlotParRow];
        // body
        for (int i = 0; i < numberOfRow; i++) {
            for (int j = 0; j < numberOfSlotParRow; j++) {
                int index = i * numberOfSlotParRow + j;
                int itemHash = byteBuffer.getInt();
                if (itemHash == 0) {
                    itemsRegistry[index] = null;
                    ByteBufferHelper.readUUID(byteBuffer);
                } else {
                    itemsRegistry[index] = ItemRegisterEntry.getItemByHash(itemHash);
                    itemsUuid[index] = ByteBufferHelper.readUUID(byteBuffer);
                }
                itemsNumber[index] = byteBuffer.get();
            }
        }
        return (itemManager) -> {
            int[][] inventory = new int[numberOfRow * numberOfSlotParRow][InventoryComponent.DEFAULT_STACK_LIMITE];
            for (int i = 0; i < numberOfRow; i++) {
                for (int j = 0; j < numberOfSlotParRow; j++) {
                    int index = i * numberOfSlotParRow + j;
                    ItemRegisterEntry itemRegisterEntry = itemsRegistry[index];
                    UUID itemUuid = itemsUuid[index];
                    if (itemRegisterEntry != null) {
                        for (int n = 0; n < itemsNumber[index]; n++) {
                            int itemId = itemManager.newItemInventory(itemRegisterEntry, itemUuid);
                            inventory[index][n] = itemId;
                        }
                    }
                }
            }
            return inventory;
        };
    }

    private int getTailleBytes(InventoryComponent inventoryComponent) {
        // version protocole, nombre de row, slot par row, backgroundTextureName, nombre de row * slot par row * ((hash + nombre) + uuid)
        return Integer.BYTES + Integer.BYTES + Integer.BYTES + inventoryComponent.backgroundTexture.getBytes(StandardCharsets.US_ASCII).length + 1 + (inventoryComponent.numberOfRow * inventoryComponent.numberOfSlotParRow * ((Integer.BYTES + Byte.BYTES) + Long.BYTES * 2));
    }
}
