package ch.realmtech.server.serialize;

import ch.realmtech.server.serialize.cell.CellSerializerController;
import ch.realmtech.server.serialize.chest.ChestSerializerController;
import ch.realmtech.server.serialize.chunk.ChunkSerializerController;
import ch.realmtech.server.serialize.craftingtable.CraftingTableController;
import ch.realmtech.server.serialize.furnace.FurnaceSerializerController;
import ch.realmtech.server.serialize.inventory.InventorySerializerController;
import ch.realmtech.server.serialize.life.LifeSerializerController;
import ch.realmtech.server.serialize.physicEntity.PhysicEntitySerializerController;
import ch.realmtech.server.serialize.player.PlayerSerializerController;
import ch.realmtech.server.serialize.savemetadata.SaveMetadataSerializerController;
import ch.realmtech.server.serialize.uuid.UuidSerializerController;
import com.artemis.World;

import java.lang.reflect.Field;


public final class SerializerController {
    private World world;
    private final InventorySerializerController inventorySerializerManager = new InventorySerializerController(this);
    private final ChestSerializerController chestSerializerController = new ChestSerializerController(this);
    private final UuidSerializerController uuidSerializerController = new UuidSerializerController(this);
    private final CellSerializerController cellSerializerController = new CellSerializerController(this);
    private final ChunkSerializerController chunkSerializerController = new ChunkSerializerController(this);
    private final SaveMetadataSerializerController saveMetadataSerializerController = new SaveMetadataSerializerController(this);
    private final CraftingTableController craftingTableController = new CraftingTableController(this);
    private final FurnaceSerializerController furnaceSerializerController = new FurnaceSerializerController(this);
    private final PhysicEntitySerializerController physicEntitySerializerController = new PhysicEntitySerializerController(this);
    private final LifeSerializerController lifeSerializerController = new LifeSerializerController(this);
    private final PlayerSerializerController playerSerializerController = new PlayerSerializerController(this);

    public void initialize(World world) throws Exception {
        if (this.world != null) throw new IllegalCallerException("Already initialize");
        this.world = world;

        for (Field declaredField : getClass().getDeclaredFields()) {
            if (!declaredField.getName().equals("world")) {
                Object serializerControllerObject = declaredField.get(this);
                ((AbstractSerializerController<?, ?>) serializerControllerObject).getSerializers().values().forEach(world::inject);
            }
        }
    }

    public <InputType> int getApplicationBytesLength(AbstractSerializerController<InputType, ?> abstractSerializerController, InputType inputType) {
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

    public LifeSerializerController getLifeSerializerController() {
        return lifeSerializerController;
    }

    public PlayerSerializerController getPlayerSerializerController() {
        return playerSerializerController;
    }

    World getGetWorld() {
        return world;
    }
}
