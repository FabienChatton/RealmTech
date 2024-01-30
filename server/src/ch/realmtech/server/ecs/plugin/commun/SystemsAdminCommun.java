package ch.realmtech.server.ecs.plugin.commun;

import ch.realmtech.server.ecs.system.*;
import com.artemis.ArtemisPlugin;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;

public abstract class SystemsAdminCommun implements ArtemisPlugin {
    public final TagManager tagManager;
    public final UuidComponentManager uuidComponentManager;
    public final InventoryManager inventoryManager;
    public final MapManager mapManager;
    public final SaveInfManager saveInfManager;
    public final EnergyManager energyManager;
    public final CellPaddingManager cellPaddingManager;

    public SystemsAdminCommun() {
        tagManager = new TagManager();
        uuidComponentManager = new UuidComponentManager();
        inventoryManager = new InventoryManager();
        mapManager = new MapManager();
        saveInfManager = new SaveInfManager();
        energyManager = new EnergyManager();
        cellPaddingManager = new CellPaddingManager();
    }

    @Override
    public void setup(WorldConfigurationBuilder b) {
        b.with(
                tagManager,
                uuidComponentManager,
                inventoryManager,
                mapManager,
                saveInfManager,
                energyManager,
                cellPaddingManager
        );
    }

    public abstract void onContextType(ContextType contextType, Runnable runnable);
}
