package ch.realmtech.server.serialize;

import ch.realmtech.server.serialize.chest.ChestSerializerController;
import ch.realmtech.server.serialize.inventory.InventorySerializerController;
import ch.realmtech.server.serialize.uuid.UuidSerializerController;
import com.artemis.World;


public class SerializerController {
    private final InventorySerializerController inventorySerializerManager = new InventorySerializerController(this);
    private final ChestSerializerController chestSerializerController = new ChestSerializerController(this);
    private final UuidSerializerController uuidSerializerController = new UuidSerializerController(this);

    public <InputType> int getApplicationBytesLength(World world, AbstractSerializerController<InputType, ?> abstractSerializerController, InputType inputType) {
        int rawBytesLength = abstractSerializerController.getSerializer().getBytesSize(world, this, inputType);
        return AbstractSerializerController.MAGIC_NUMBER_LENGTH + AbstractSerializerController.VERSION_LENGTH + rawBytesLength;
    }

    public InventorySerializerController getInventorySerializerManager() {
        return inventorySerializerManager;
    }

    public ChestSerializerController getChestSerializerController() {
        return chestSerializerController;
    }

    public UuidSerializerController getUuidSerializerController() {
        return uuidSerializerController;
    }
}
