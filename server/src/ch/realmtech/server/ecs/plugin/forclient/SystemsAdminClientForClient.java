package ch.realmtech.server.ecs.plugin.forclient;

import ch.realmtech.server.ecs.LightManagerForClient;
import ch.realmtech.server.ecs.system.CraftingManager;

public interface SystemsAdminClientForClient {
    LightManagerForClient getLightManager();
    FurnaceIconSystemForClient getFurnaceIconSystem();
    CraftingManager getCraftingManager();

    EnergyIconSystemForClient getEnergyBatteryIconSystem();
}