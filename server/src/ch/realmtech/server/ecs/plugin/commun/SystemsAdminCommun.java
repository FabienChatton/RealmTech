package ch.realmtech.server.ecs.plugin.commun;

import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.ecs.system.SaveInfManager;
import com.artemis.ArtemisPlugin;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;

public abstract class SystemsAdminCommun implements ArtemisPlugin {
    public final TagManager tagManager;
    public final InventoryManager inventoryManager;
    public final MapManager mapManager;
    public final SaveInfManager saveInfManager;

    public SystemsAdminCommun() {
        tagManager = new TagManager();
        inventoryManager = new InventoryManager();
        mapManager = new MapManager();
        saveInfManager = new SaveInfManager();
    }

    @Override
    public void setup(WorldConfigurationBuilder b) {
        b.with(
                tagManager,
                inventoryManager,
                mapManager,
                saveInfManager
        );
    }
}
