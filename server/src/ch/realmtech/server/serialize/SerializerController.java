package ch.realmtech.server.serialize;

import ch.realmtech.server.ecs.GetWorld;
import ch.realmtech.server.serialize.cell.CellSerializerController;
import ch.realmtech.server.serialize.chest.ChestSerializerController;
import ch.realmtech.server.serialize.chunk.ChunkSerializerController;
import ch.realmtech.server.serialize.inventory.InventorySerializerController;
import ch.realmtech.server.serialize.savemetadata.SaveMetadataSerializerController;
import ch.realmtech.server.serialize.uuid.UuidSerializerController;
import com.artemis.World;


public class SerializerController {
    private final GetWorld getWorld;
    private final InventorySerializerController inventorySerializerManager = new InventorySerializerController(this);
    private final ChestSerializerController chestSerializerController = new ChestSerializerController(this);
    private final UuidSerializerController uuidSerializerController = new UuidSerializerController(this);
    private final CellSerializerController cellSerializerController = new CellSerializerController(this);
    private final ChunkSerializerController chunkSerializerController = new ChunkSerializerController(this);
    private final SaveMetadataSerializerController saveMetadataSerializerController = new SaveMetadataSerializerController(this);

    public SerializerController(GetWorld getWorld) {
        this.getWorld = getWorld;
    }

    public <InputType> int getApplicationBytesLength(AbstractSerializerController<InputType, ?> abstractSerializerController, InputType inputType) {
        int rawBytesLength = abstractSerializerController.getSerializer().getBytesSize(getWorld.getWorld(), this, inputType);
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

    public CellSerializerController getCellSerializerController() {
        return cellSerializerController;
    }

    public ChunkSerializerController getChunkSerializerController() {
        return chunkSerializerController;
    }

    public SaveMetadataSerializerController getSaveMetadataSerializerController() {
        return saveMetadataSerializerController;
    }

    protected World getGetWorld() {
        return getWorld.getWorld();
    }
}
