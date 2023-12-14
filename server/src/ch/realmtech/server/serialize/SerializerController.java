package ch.realmtech.server.serialize;

import ch.realmtech.server.ecs.GetWorld;
import ch.realmtech.server.serialize.cell.CellSerializerController;
import ch.realmtech.server.serialize.chest.ChestSerializerController;
import ch.realmtech.server.serialize.chunk.ChunkSerializerController;
import ch.realmtech.server.serialize.craftingtable.CraftingTableController;
import ch.realmtech.server.serialize.furnace.FurnaceSerializerController;
import ch.realmtech.server.serialize.inventory.InventorySerializerController;
import ch.realmtech.server.serialize.physicEntity.PhysicEntitySerializerController;
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
    private final CraftingTableController craftingTableController = new CraftingTableController(this);
    private final FurnaceSerializerController furnaceSerializerController = new FurnaceSerializerController(this);
    private final PhysicEntitySerializerController physicEntitySerializerController = new PhysicEntitySerializerController(this);

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

    public CraftingTableController getCraftingTableController() {
        return craftingTableController;
    }

    public FurnaceSerializerController getFurnaceSerializerController() {
        return furnaceSerializerController;
    }

    public PhysicEntitySerializerController getPhysicEntitySerializerController() {
        return physicEntitySerializerController;
    }

    World getGetWorld() {
        return getWorld.getWorld();
    }
}
