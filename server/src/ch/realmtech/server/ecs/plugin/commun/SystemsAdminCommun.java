package ch.realmtech.server.ecs.plugin.commun;

import ch.realmtech.server.ecs.system.*;
import ch.realmtech.server.registry.Entry;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.Registry;
import com.artemis.ArtemisPlugin;
import com.artemis.BaseSystem;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.utils.Disposable;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class SystemsAdminCommun extends Entry implements ArtemisPlugin, SystemsAdminCommunItf, Disposable {
    private Registry<?> rootRegistry;
    private final HashMap<Class<? extends BaseSystem>, Map.Entry<Integer, Supplier<? extends BaseSystem>>> customSystems;
    private HashMap<Class<? extends BaseSystem>, BaseSystem> customSystemsInstance;

    public SystemsAdminCommun(String name) {
        super(name);
        customSystems = new HashMap<>();
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        this.rootRegistry = rootRegistry;

        putCustomSystem(10, TagManager.class, TagManager::new);
        putCustomSystem(10, UuidEntityManager.class, UuidEntityManager::new);
        putCustomSystem(10, InventoryManager.class, InventoryManager::new);
        putCustomSystem(10, MapManager.class, MapManager::new);
        putCustomSystem(10, SaveInfManager.class, SaveInfManager::new);
        putCustomSystem(10, EnergyManager.class, EnergyManager::new);
        putCustomSystem(10, CellPaddingManager.class, CellPaddingManager::new);
        putCustomSystem(10, EnemyManager.class, EnemyManager::new);
    }

    @Override
    public void setup(WorldConfigurationBuilder b) {
        customSystemsInstance = new HashMap<>();
        for (Class<? extends BaseSystem> customSystemClass : customSystems.keySet()) {
            Map.Entry<Integer, Supplier<? extends BaseSystem>> customSystem = customSystems.get(customSystemClass);
            BaseSystem systemInstance = customSystem.getValue().get();

            b.with(Integer.MAX_VALUE - customSystem.getKey(), systemInstance);
            customSystemsInstance.put(customSystemClass, systemInstance);
        }
    }

    public abstract void onContextType(ContextType contextType, Runnable runnable);

    public Registry<?> getRootRegistry() {
        return rootRegistry;
    }

    @Override
    public TagManager getTagManager() {
        return getCustomSystem(TagManager.class);
    }

    @Override
    public UuidEntityManager getUuidEntityManager() {
        return getCustomSystem(UuidEntityManager.class);
    }

    @Override
    public InventoryManager getInventoryManager() {
        return getCustomSystem(InventoryManager.class);
    }

    @Override
    public MapManager getMapManager() {
        return getCustomSystem(MapManager.class);
    }

    @Override
    public SaveInfManager getSaveInfManager() {
        return getCustomSystem(SaveInfManager.class);
    }

    @Override
    public EnergyManager getEnergyManager() {
        return getCustomSystem(EnergyManager.class);
    }

    @Override
    public CellPaddingManager getCellPaddingManager() {
        return getCustomSystem(CellPaddingManager.class);
    }

    @Override
    public EnemyManager getMobManager() {
        return getCustomSystem(EnemyManager.class);
    }

    public <T extends BaseSystem> void putCustomSystem(int order, Class<? extends T> customSystemClass, Supplier<? extends T> customSystemSupplier) {
        customSystems.put(customSystemClass, new AbstractMap.SimpleEntry<>(order, customSystemSupplier));
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseSystem> T getCustomSystem(Class<T> customSystemClazz) {
        if (customSystemsInstance == null) {
            return null;
        } else {
            return (T) customSystemsInstance.get(customSystemClazz);
        }
    }

    @Override
    public void dispose() {
        customSystemsInstance = null;
    }
}
