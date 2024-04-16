package ch.realmtech.server.serialize;

import ch.realmtech.server.serialize.cell.CellSerializerController;
import ch.realmtech.server.serialize.chest.ChestSerializerController;
import ch.realmtech.server.serialize.chunk.ChunkSerializerController;
import ch.realmtech.server.serialize.craftingtable.CraftingTableController;
import ch.realmtech.server.serialize.energyBattery.EnergyBatterySerializerController;
import ch.realmtech.server.serialize.energyCable.EnergyCableSerializerController;
import ch.realmtech.server.serialize.energyGenerator.EnergyGeneratorSerializerController;
import ch.realmtech.server.serialize.face.FaceSerializerController;
import ch.realmtech.server.serialize.furnace.FurnaceSerializerController;
import ch.realmtech.server.serialize.inventory.InventorySerializerController;
import ch.realmtech.server.serialize.life.LifeSerializerController;
import ch.realmtech.server.serialize.physicEntity.PhysicEntitySerializerController;
import ch.realmtech.server.serialize.player.PlayerSerializerController;
import ch.realmtech.server.serialize.quests.QuestSerializerController;
import ch.realmtech.server.serialize.savemetadata.SaveMetadataSerializerController;
import ch.realmtech.server.serialize.uuid.UuidSerializerController;
import com.artemis.World;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public final class SerializerController {
    @IgnoreField
    private World world;
    @IgnoreField
    private final List<AbstractSerializerController<?, ?>> serializerControllers = new ArrayList<>();
    private final InventorySerializerController inventorySerializerManager = registerSerializerController(new InventorySerializerController(this));
    private final ChestSerializerController chestSerializerController = registerSerializerController(new ChestSerializerController(this));
    private final UuidSerializerController uuidSerializerController = registerSerializerController(new UuidSerializerController(this));
    private final CellSerializerController cellSerializerController = registerSerializerController(new CellSerializerController(this));
    private final ChunkSerializerController chunkSerializerController = registerSerializerController(new ChunkSerializerController(this));
    private final SaveMetadataSerializerController saveMetadataSerializerController = registerSerializerController(new SaveMetadataSerializerController(this));
    private final CraftingTableController craftingTableController = registerSerializerController(new CraftingTableController(this));
    private final FurnaceSerializerController furnaceSerializerController = registerSerializerController(new FurnaceSerializerController(this));
    private final PhysicEntitySerializerController physicEntitySerializerController = registerSerializerController(new PhysicEntitySerializerController(this));
    private final LifeSerializerController lifeSerializerController = registerSerializerController(new LifeSerializerController(this));
    private final PlayerSerializerController playerSerializerController = registerSerializerController(new PlayerSerializerController(this));
    private final FaceSerializerController faceSerializerController = registerSerializerController(new FaceSerializerController(this));
    private final EnergyBatterySerializerController energyBatterySerializerController = registerSerializerController(new EnergyBatterySerializerController(this));
    private final EnergyCableSerializerController energyCableSerializerController = registerSerializerController(new EnergyCableSerializerController(this));
    private final EnergyGeneratorSerializerController energyGeneratorSerializerController = registerSerializerController(new EnergyGeneratorSerializerController(this));
    private final QuestSerializerController questSerializerController = registerSerializerController(new QuestSerializerController(this));

    public void initialize(World world) throws Exception {
        if (this.world != null) throw new IllegalCallerException("Already initialize");
        this.world = world;

        for (Field declaredField : getClass().getDeclaredFields()) {
            if (declaredField.getDeclaredAnnotation(IgnoreField.class) == null) {
                Object serializerControllerObject = declaredField.get(this);
                ((AbstractSerializerController<?, ?>) serializerControllerObject).getSerializers().values().forEach(world::inject);
            }
        }
    }

    private <T extends AbstractSerializerController<?, ?>> T registerSerializerController(T serializerController) {
        serializerControllers.add(serializerController);
        return serializerController;
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

    public FaceSerializerController getFaceSerializerController() {
        return faceSerializerController;
    }

    public EnergyBatterySerializerController getEnergyBatterySerializerController() {
        return energyBatterySerializerController;
    }

    public EnergyCableSerializerController getEnergyCableSerializerController() {
        return energyCableSerializerController;
    }

    public EnergyGeneratorSerializerController getEnergyGeneratorSerializerController() {
        return energyGeneratorSerializerController;
    }

    public QuestSerializerController getQuestSerializerController() {
        return questSerializerController;
    }

    @SuppressWarnings("unchecked")
    public <OutputType> AbstractSerializerController<?, OutputType> getSerializerControllerByMagic(byte magicNumber) {
        for (AbstractSerializerController<?, ?> serializerController : serializerControllers) {
            if (serializerController.getMagicNumber() == magicNumber) {
                return (AbstractSerializerController<?, OutputType>) serializerController;
            }
        }
        throw new NoSuchElementException("Can not find serializer controller by magic number. Magic number: " + magicNumber);
    }

    World getGetWorld() {
        return world;
    }
}
