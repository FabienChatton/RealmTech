package ch.realmtech.server.ecs.plugin.commun;

import ch.realmtech.server.ecs.system.*;
import com.artemis.managers.TagManager;

public interface SystemsAdminCommunItf {
    TagManager getTagManager();

    UuidEntityManager getUuidEntityManager();

    InventoryManager getInventoryManager();

    MapManager getMapManager();

    SaveInfManager getSaveInfManager();

    EnergyManager getEnergyManager();

    CellPaddingManager getCellPaddingManager();

    MobManager getMobManager();
}
