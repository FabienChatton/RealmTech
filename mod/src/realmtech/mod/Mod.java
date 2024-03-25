package realmtech.mod;

import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.mod.AssetsProvider;
import ch.realmtech.server.mod.ModInitializer;
import ch.realmtech.server.registry.Registry;

@AssetsProvider
public class Mod implements ModInitializer {

    @Override
    public String getModId() {
        return "test";
    }

    @Override
    public void initializeModRegistry(Registry<?> modRegistry, Context context) {
        System.out.println("oui");
    }
}
