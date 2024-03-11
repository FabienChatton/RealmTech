package ch.realmtech.server.newMod;

import ch.realmtech.server.newRegistry.NewRegistry;

public interface ModInitializer {
    String getModId();

    void initializeModRegistry(NewRegistry<?> modRegistry);
}
