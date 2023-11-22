package ch.realmtech.server.serialize.chest;

import ch.realmtech.server.ecs.component.InventoryChestComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import com.artemis.ComponentMapper;
import com.artemis.World;

public class ChestSerializerV1 implements Serializer<InventoryChestComponent, Integer> {
    @Override
    public byte[] toBytes(World world, SerializerController serializerController, InventoryChestComponent chestComponent) {
        return new byte[0];
    }

    @Override
    public Integer fromBytes(World world, SerializerController serializerController, byte[] bytes) {
        int chestId = world.create();
        return null;
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, InventoryChestComponent chestComponent) {
        ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);
        InventoryComponent chestInventoryComponent = mInventory.get(chestComponent.getInventoryId());
        int bytesSizeInventory = serializerController.getInventorySerializerManager().getBytesSize(world, serializerController, chestInventoryComponent);
        // inventory, uuid
        return bytesSizeInventory + Long.BYTES * 2;
    }
}
