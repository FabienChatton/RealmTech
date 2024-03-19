package ch.realmtech.server.mod;

import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.registry.Registry;

public interface ModInitializer {
    String getModId();

    void initializeModRegistry(Registry<?> modRegistry, Context context);
}
