package ch.realmtechServer.serialize.inventory;

import ch.realmtechServer.ctrl.ItemManager;
import ch.realmtechServer.divers.ByteBufferGetString;
import ch.realmtechServer.ecs.component.InventoryComponent;
import ch.realmtechServer.ecs.component.ItemComponent;
import ch.realmtechServer.ecs.system.InventoryManager;
import ch.realmtechServer.registery.ItemRegisterEntry;
import com.artemis.ComponentMapper;
import com.artemis.World;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class InventorySerializerV1 implements InventorySerializer {
    private final static int VERSION = 1;
    @Override
    public byte[] toBytes(InventoryComponent inventoryComponent, World world) {
        ComponentMapper<ItemComponent> mItem = world.getMapper(ItemComponent.class);
        // header
        ByteBuffer byteBuffer = ByteBuffer.allocate(getTailleBytes(inventoryComponent));
        byteBuffer.putInt(VERSION);
        byteBuffer.putInt(inventoryComponent.numberOfRow);
        byteBuffer.putInt(inventoryComponent.numberOfSlotParRow);
        byteBuffer.put((inventoryComponent.backgroundTexture + "\0").getBytes(StandardCharsets.US_ASCII));
        // body
        for (int i = 0; i < inventoryComponent.numberOfRow; i++) {
            for (int j = 0; j < inventoryComponent.numberOfSlotParRow; j++) {
                // si pas d'item dans le slot, met 0
                if (inventoryComponent.inventory[i][j] == 0) {
                    byteBuffer.putInt(0);
                } else {
                    byteBuffer.putInt(ItemRegisterEntry.getHash(mItem.get(inventoryComponent.inventory[i][j]).itemRegisterEntry));
                }
                byteBuffer.put((byte) InventoryManager.tailleStack(inventoryComponent.inventory[i]));
            }
        }

        return byteBuffer.array();
    }

    @Override
    public Supplier<Integer> fromBytes(World world, byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        // header
        int version = byteBuffer.getInt();
        int numberOfRow = byteBuffer.getInt();
        int numberOfSlotParRow = byteBuffer.getInt();
        String backgroundTextureName = ByteBufferGetString.getString(byteBuffer);
        ItemRegisterEntry[] itemsRegistry = new ItemRegisterEntry[numberOfRow * numberOfSlotParRow];
        byte[] itemsNumber = new byte[numberOfRow * numberOfSlotParRow];
        // body
        for (int i = 0; i < numberOfRow; i++) {
            for (int j = 0; j < numberOfSlotParRow; j++) {
                int index = j * (i + 1);
                itemsRegistry[index] = ItemRegisterEntry.getItemByHash(byteBuffer.getInt());
                itemsNumber[index] = byteBuffer.get();
            }
        }
        return () -> {
            int inventoryId = world.create();
            InventoryComponent inventoryComponent = world.edit(inventoryId).create(InventoryComponent.class).set(numberOfSlotParRow, numberOfRow, backgroundTextureName);
            for (int i = 0; i < numberOfRow; i++) {
                for (int j = 0; j < numberOfSlotParRow; j++) {
                    int index = j * (i + 1);
                    ItemRegisterEntry itemRegisterEntry = itemsRegistry[index];
                    if (itemRegisterEntry != null) {
                        for (int n = 0; n < itemsNumber[index]; n++) {
                            int itemId = world.getSystem(ItemManager.class).newItemInventory(itemRegisterEntry);
                            inventoryComponent.inventory[index][n] = itemId;
                        }
                    }
                }
            }
            return inventoryId;
        };
    }

    private int getTailleBytes(InventoryComponent inventoryComponent) {
        // version protocole, nombre de row, slot par row, backgroundTextureName, nombre de row * slot par row * (hash + nombre)
        return Integer.BYTES + Integer.BYTES + Integer.BYTES + inventoryComponent.backgroundTexture.getBytes(StandardCharsets.UTF_8).length + 1 + inventoryComponent.numberOfRow * inventoryComponent.numberOfSlotParRow * Integer.BYTES + Byte.BYTES;
    }
}
