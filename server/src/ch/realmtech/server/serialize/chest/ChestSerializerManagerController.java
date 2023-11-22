package ch.realmtech.server.serialize.chest;

import ch.realmtech.server.ecs.component.InventoryChestComponent;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import com.artemis.World;

import java.nio.charset.StandardCharsets;

public class ChestSerializerManagerController implements ChestSerializerManager {
    private final static Serializer<InventoryChestComponent, Integer> CHEST_SERIALIZER_1 = new ChestSerializerV1();
    @Override
    public byte[] toBytesLatest(World world, SerializerController serializerController, InventoryChestComponent toSerialize) {
        return CHEST_SERIALIZER_1.toBytes(world, serializerController, toSerialize);
    }

    @Override
    public Integer fromBytes(World world, SerializerController serializerController, byte[] bytes) {
        return null;
    }

    @Override
    public Serializer<InventoryChestComponent, Integer> getSerializerLatest() {
        return CHEST_SERIALIZER_1;
    }

    @Override
    public byte[] getMagicNumbers() {
        return "chestInv\0".getBytes(StandardCharsets.UTF_8);
    }
}
