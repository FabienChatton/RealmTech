package ch.realmtech.server.ecs.plugin.commun;

import ch.realmtech.server.ecs.system.*;
import ch.realmtech.server.registry.Entry;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.Registry;
import com.artemis.ArtemisPlugin;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;

public abstract class SystemsAdminCommun extends Entry implements ArtemisPlugin, SystemsAdminCommunItf {
    private TagManager tagManager;
    private UuidEntityManager uuidEntityManager;
    private InventoryManager inventoryManager;
    private MapManager mapManager;
    private SaveInfManager saveInfManager;
    private EnergyManager energyManager;
    private CellPaddingManager cellPaddingManager;
    private MobManager mobManager;
    private Registry<?> rootRegistry;

    public SystemsAdminCommun(String name) {
        super(name);
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        this.rootRegistry = rootRegistry;

        setTagManager(new TagManager());
        setUuidEntityManager(new UuidEntityManager());
        setInventoryManager(new InventoryManager());
        setMapManager(new MapManager());
        setSaveInfManager(new SaveInfManager());
        setEnergyManager(new EnergyManager());
        setCellPaddingManager(new CellPaddingManager());
        setMobManager(new MobManager());
    }

    @Override
    public void setup(WorldConfigurationBuilder b) {
        b.with(
                getTagManager(),
                getUuidEntityManager(),
                getInventoryManager(),
                getMapManager(),
                getSaveInfManager(),
                getEnergyManager(),
                getCellPaddingManager(),
                getMobManager()
        );
    }

    public abstract void onContextType(ContextType contextType, Runnable runnable);

    public Registry<?> getRootRegistry() {
        return rootRegistry;
    }

    @Override
    public TagManager getTagManager() {
        return tagManager;
    }

    public void setTagManager(TagManager tagManager) {
        this.tagManager = tagManager;
    }

    @Override
    public UuidEntityManager getUuidEntityManager() {
        return uuidEntityManager;
    }

    public void setUuidEntityManager(UuidEntityManager uuidEntityManager) {
        this.uuidEntityManager = uuidEntityManager;
    }

    @Override
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public void setInventoryManager(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    @Override
    public MapManager getMapManager() {
        return mapManager;
    }

    public void setMapManager(MapManager mapManager) {
        this.mapManager = mapManager;
    }

    @Override
    public SaveInfManager getSaveInfManager() {
        return saveInfManager;
    }

    public void setSaveInfManager(SaveInfManager saveInfManager) {
        this.saveInfManager = saveInfManager;
    }

    @Override
    public EnergyManager getEnergyManager() {
        return energyManager;
    }

    public void setEnergyManager(EnergyManager energyManager) {
        this.energyManager = energyManager;
    }

    @Override
    public CellPaddingManager getCellPaddingManager() {
        return cellPaddingManager;
    }

    public void setCellPaddingManager(CellPaddingManager cellPaddingManager) {
        this.cellPaddingManager = cellPaddingManager;
    }

    @Override
    public MobManager getMobManager() {
        return mobManager;
    }

    public void setMobManager(MobManager mobManager) {
        this.mobManager = mobManager;
    }
}
