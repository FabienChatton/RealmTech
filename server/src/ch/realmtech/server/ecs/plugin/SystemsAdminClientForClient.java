package ch.realmtech.server.ecs.plugin;

import ch.realmtech.server.ecs.LightManagerForClient;

public interface SystemsAdminClientForClient {
    LightManagerForClient getLightManager();
    FurnaceIconSystemForClient getFurnaceIconSystem();
}
