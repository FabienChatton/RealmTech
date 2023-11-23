package ch.realmtech.server.serialize;

import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.serialize.inventory.InventorySerializer;
import ch.realmtech.server.serialize.inventory.InventorySerializerControllerItf;
import ch.realmtech.server.serialize.inventory.InventorySerializerV3;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.World;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class InventorySerializerController implements InventorySerializerControllerItf {

    private final SerializerController serializerController;
    private final static Map<Integer, InventorySerializer> INVENTORY_SERIALIZERS_DECODER = new HashMap<>();
    private final static int LATEST_VERSION = 3;

    static {
        INVENTORY_SERIALIZERS_DECODER.put(3, new InventorySerializerV3());
    }

    public InventorySerializerController(SerializerController serializerController) {
        this.serializerController = serializerController;
    }

    @Override
    public SerializedApplicationBytes encode(World world, InventoryComponent toSerialize) {
        return serializerController.encodeApplicationBytes(world, this, toSerialize);
    }

    @Override
    public Function<ItemManager, int[][]> decode(World world, SerializedApplicationBytes applicationBytes) {
        return serializerController.decodeApplicationBytes(world, this, applicationBytes);
    }

    @Override
    public InventorySerializer getSerializer() {
        return INVENTORY_SERIALIZERS_DECODER.get(LATEST_VERSION);
    }

    public InventorySerializer getSerializer(int version) {
        return INVENTORY_SERIALIZERS_DECODER.get(version);
    }

    public byte[] getMagicNumbers() {
        return "invBytes\0".getBytes(StandardCharsets.UTF_8);
    }
}
