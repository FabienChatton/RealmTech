package ch.realmtech.server.ecs.plugin.forclient;

import ch.realmtech.server.ecs.LightManagerForClient;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommunItf;
import ch.realmtech.server.ecs.system.CraftingManager;
import ch.realmtech.server.registry.Registry;

public interface SystemsAdminClientForClient extends SystemsAdminCommunItf {
    LightManagerForClient getLightManager();
    FurnaceIconSystemForClient getFurnaceIconSystem();
    CraftingManager getCraftingManager();

    EnergyIconSystemForClient getEnergyBatteryIconSystem();

    Registry<?> getRootRegistry();
}
