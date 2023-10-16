package ch.realmtechServer.serialize.inventory;

import ch.realmtechServer.ecs.component.InventoryComponent;
import ch.realmtechServer.ecs.component.ItemComponent;
import com.artemis.ComponentMapper;
import com.artemis.World;

import java.nio.ByteBuffer;
import java.util.function.BiFunction;

public class InventorySerializerV1 implements BiFunction<InventoryComponent, World, byte[]> {
    private final static int VERSION = 1;
    @Override
    public byte[] apply(InventoryComponent inventoryComponent, World world) {
        ComponentMapper<ItemComponent> mItem = world.getMapper(ItemComponent.class);
        // header
        ByteBuffer byteBuffer = ByteBuffer.allocate(getTailleBytes(inventoryComponent));
        byteBuffer.putInt(VERSION);
        byteBuffer.putInt(inventoryComponent.numberOfRow);
        byteBuffer.putInt(inventoryComponent.numberOfSlotParRow);
        for (int i = 0; i < inventoryComponent.inventory.length; i++) {
            for (int j = 0; j < inventoryComponent.inventory[i].length; j++) {
                // si pas d'item dans le slot, met 0
                if (inventoryComponent.inventory[i][j] == 0) {
                    byteBuffer.putInt(0);
                } else {

                }
            }
        }

        return byteBuffer.array();
    }

    private int getTailleBytes(InventoryComponent inventoryComponent) {
        // version protocole, nombre de row, slot par row, nombre de row * slot par row * (hash + nombre)
        return Integer.BYTES + Integer.BYTES + Integer.BYTES + inventoryComponent.numberOfRow * inventoryComponent.numberOfSlotParRow * Integer.BYTES + Byte.BYTES;
    }
}
