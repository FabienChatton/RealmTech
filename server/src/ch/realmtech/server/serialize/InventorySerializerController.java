package ch.realmtech.server.serialize;

import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.serialize.inventory.InventorySerializer;
import ch.realmtech.server.serialize.inventory.InventorySerializerCoder;
import ch.realmtech.server.serialize.inventory.InventorySerializerV3;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class InventorySerializerController extends AbstractSerializerController<InventoryComponent, Function<ItemManager, int[][]>> implements InventorySerializerCoder {
    private final static Map<Integer, InventorySerializer> INVENTORY_SERIALIZERS_DECODER = new HashMap<>();
    private final static int LATEST_VERSION = 3;

    static {
        INVENTORY_SERIALIZERS_DECODER.put(3, new InventorySerializerV3());
    }

    public InventorySerializerController(SerializerController serializerController) {
        super(serializerController, "invBytes\0".getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public InventorySerializer getSerializer() {
        return INVENTORY_SERIALIZERS_DECODER.get(LATEST_VERSION);
    }

    public InventorySerializer getSerializer(int version) {
        return INVENTORY_SERIALIZERS_DECODER.get(version);
    }
}
