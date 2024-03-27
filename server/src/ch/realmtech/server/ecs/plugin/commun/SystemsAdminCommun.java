package ch.realmtech.server.ecs.plugin.commun;

import ch.realmtech.server.ecs.system.*;
import ch.realmtech.server.registry.Entry;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.Registry;
import com.artemis.ArtemisPlugin;
import com.artemis.BaseSystem;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

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
    private final HashMap<Class<? extends BaseSystem>, Map.Entry<Integer, BaseSystem>> customSystems;

    public SystemsAdminCommun(String name) {
        super(name);
        customSystems = new HashMap<>();
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        this.rootRegistry = rootRegistry;

        setTagManager(putCustomSystem(10, new TagManager()));
        setUuidEntityManager(putCustomSystem(10, new UuidEntityManager()));
        setInventoryManager(putCustomSystem(10, new InventoryManager()));
        setMapManager(putCustomSystem(10, new MapManager()));
        setSaveInfManager(putCustomSystem(10, new SaveInfManager()));
        setEnergyManager(putCustomSystem(10, new EnergyManager()));
        setCellPaddingManager(putCustomSystem(10, new CellPaddingManager()));
        setMobManager(putCustomSystem(10, new MobManager()));
    }

    @Override
    public void setup(WorldConfigurationBuilder b) {
        for (Map.Entry<Integer, BaseSystem> customSystem : customSystems.values()) {
            b.with(Integer.MAX_VALUE - customSystem.getKey(), customSystem.getValue());
        }
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

    public <T extends BaseSystem> T putCustomSystem(int order, T customSystem) {
        BaseSystem alreadyPresentSystem = null;
        for (Class<? extends BaseSystem> customSystemClazz : customSystems.keySet()) {
            if (customSystemClazz.isInstance(customSystem)) {
                alreadyPresentSystem = getCustomSystem(customSystemClazz);
            }
        }
        if (alreadyPresentSystem != null) {
            customSystems.remove(alreadyPresentSystem.getClass());
        }
        customSystems.put(customSystem.getClass(), new AbstractMap.SimpleEntry<>(order, customSystem));
        return customSystem;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseSystem> T getCustomSystem(Class<T> customSystemClazz) {
        for (Class<? extends BaseSystem> inCustomSystemClazz : customSystems.keySet()) {
            BaseSystem inCustomSystem = customSystems.get(inCustomSystemClazz).getValue();
            if (customSystemClazz.isInstance(inCustomSystem)) {
                return (T) inCustomSystem;
            }
        }
        return null;
    }
}
