package ch.realmtech.server.serialize.inventory;

import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.registery.ItemRegisterEntry;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.annotations.Wire;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.function.Function;

public class InventorySerializerV3 implements Serializer<InventoryComponent, Function<ItemManager, InventoryArgs>> {
    private ComponentMapper<ItemComponent> mItem;
    @Wire(name = "systemsAdmin")
    private SystemsAdminCommun systemsAdminCommun;
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, InventoryComponent inventoryComponent) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(getBytesSize(world, serializerController, inventoryComponent));
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
                int itemTemoin = stack[0];
                if (itemTemoin != 0) {
                    int stackItemIndex = 0;
                    byteBuffer.putInt(index);
                    // hash
                    byteBuffer.putInt(ItemRegisterEntry.getHash(mItem.get(itemTemoin).itemRegisterEntry));
                    int numberOfItem = InventoryManager.tailleStack(stack);
                    byteBuffer.putInt(numberOfItem);
                    for (int n = 0; n < numberOfItem; n++) {
                        UUID itemUuid = systemsAdminCommun.uuidEntityManager.getEntityUuid(stack[stackItemIndex]);
                        stackItemIndex++;
                        ByteBufferHelper.writeUUID(byteBuffer, itemUuid);
                    }
                }
            }
        }
        return new SerializedRawBytes(byteBuffer.array());
    }

    @Override
    public Function<ItemManager, InventoryArgs> fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(rawBytes.rawBytes());
        // header
        int numberOfRow = byteBuffer.getInt();
        int numberOfSlotParRow = byteBuffer.getInt();
        int stackLimite = byteBuffer.getInt();
        int numberNotEmtpySlot = byteBuffer.getInt();
        int numberOfItems = byteBuffer.getInt();

        int[] inventoryIndexs = new int[numberNotEmtpySlot];
        int[] numberItemsInStack = new int[numberNotEmtpySlot];
        ItemRegisterEntry[] itemsRegistry = new ItemRegisterEntry[numberNotEmtpySlot];
        UUID[] itemsUuid = new UUID[numberOfItems];
        int itemIndex = 0;
        // body
        for (int i = 0; i < numberNotEmtpySlot; i++) {
            int inventoryIndex = byteBuffer.getInt();
            inventoryIndexs[i] = inventoryIndex;
            int hash = byteBuffer.getInt();
            itemsRegistry[i] = ItemRegisterEntry.getItemByHash(hash);
            int numberOfItem = byteBuffer.getInt();
            numberItemsInStack[i] = numberOfItem;
            for (int n = 0; n < numberOfItem; n++) {
                UUID uuid = ByteBufferHelper.readUUID(byteBuffer);
                itemsUuid[itemIndex] = uuid;
                ++itemIndex;
            }
        }


        return (itemManager) -> {
            int uuidIndex = 0;
            int[][] inventory = new int[numberOfRow * numberOfSlotParRow][InventoryComponent.DEFAULT_STACK_LIMITE];
            for (int i = 0; i < numberNotEmtpySlot; i++) {
                ItemRegisterEntry itemRegisterEntry = itemsRegistry[i];
                for (int n = 0; n < numberItemsInStack[i]; n++) {
                    UUID uuid = itemsUuid[uuidIndex];
                    uuidIndex++;
                    int newItem = itemManager.newItemInventory(itemRegisterEntry, uuid);
                    world.getSystem(InventoryManager.class).addItemToStack(inventory[inventoryIndexs[i]], newItem);
                }
            }
            return new InventoryArgs(inventory, numberOfRow, numberOfSlotParRow);
        };
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, InventoryComponent inventoryComponent) {
        int numberofNotEmptySlot = getNumberNotEmtpySlot(inventoryComponent);
        int numberOfItems = getNumberOfItems(inventoryComponent);
        // number of row, number of column, stack size, number of not empty stack, total number of items, number of not empty stack * (hash + number item stack + index), number of items * (number item stack + hash + uuid)
        return Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES + numberofNotEmptySlot * (Integer.BYTES + Integer.BYTES + Integer.BYTES) + numberOfItems * (Integer.BYTES + Integer.BYTES + (Long.BYTES * 2));
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

    @Override
    public byte getVersion() {
        return 3;
    }
}
