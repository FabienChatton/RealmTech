package ch.realmtech.server.serialize.inventory;

import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import com.artemis.World;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class InventorySerializerManagerController implements InventorySerializerManager {
    private final static Serializer<InventoryComponent, Function<ItemManager, int[][]>> INVENTORY_SERIALIZER_2 = new InventorySerializerV2();

    public byte[] toBytesLatest(World world, SerializerController serializerController, InventoryComponent inventoryComponent) {
        return getSerializerLatest().toBytes(world, serializerController, inventoryComponent);
    }

    public Function<ItemManager, int[][]> fromBytes(World world, SerializerController serializerController, byte[] bytes) {
        byte[] versionBytes = new byte[Integer.BYTES];
        System.arraycopy(bytes, 0, versionBytes, 0, versionBytes.length);
        ByteBuffer byteBuffer = ByteBuffer.wrap(versionBytes);
        int version = byteBuffer.getInt();

        return switch (version) {
            case 2 -> INVENTORY_SERIALIZER_2.fromBytes(world, serializerController, bytes);
            default -> throw new IllegalStateException("Unexpected value: " + version + ". Cette version n'est pas implémentée");
        };
    }

    @Override
    public Serializer<InventoryComponent, Function<ItemManager, int[][]>> getSerializerLatest() {
        return INVENTORY_SERIALIZER_2;
    }

    @Override
    public byte[] getMagicNumbers() {
        return "invBytes\0".getBytes(StandardCharsets.UTF_8);
    }
}
