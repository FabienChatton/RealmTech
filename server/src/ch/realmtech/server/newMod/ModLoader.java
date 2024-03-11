package ch.realmtech.server.newMod;

import ch.realmtech.server.newRegistry.InvalideEvaluate;
import ch.realmtech.server.newRegistry.NewRegistry;
import ch.realmtech.server.newRegistry.RegistryUtils;

public class ModLoader {
    private final NewRegistry<?> rootRegistry;

    public ModLoader(NewRegistry<?> rootRegistry) {
        this.rootRegistry = rootRegistry;
    }

    public void initializeCoreMod() {
        NewRealmTechCoreMod newRealmTechCoreMod = new NewRealmTechCoreMod();
        String coreModId = newRealmTechCoreMod.getModId();
        NewRegistry<?> coreModRegistry = NewRegistry.createRegistry(rootRegistry, coreModId);

        newRealmTechCoreMod.initializeModRegistry(coreModRegistry);

        RegistryUtils.flatEntry(rootRegistry).forEach((entry) -> {
            try {
                entry.evaluate(rootRegistry);
            } catch (InvalideEvaluate e) {
                System.err.println("invalide evaluation. Error: " + e.getMessage() + "\n" + e);
            }
        });
    }
}
