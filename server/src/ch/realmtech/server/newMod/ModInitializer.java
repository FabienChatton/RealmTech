package ch.realmtech.server.newMod;

import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.newRegistry.NewRegistry;

public interface ModInitializer {
    String getModId();

    void initializeModRegistry(NewRegistry<?> modRegistry, Context context);
}
