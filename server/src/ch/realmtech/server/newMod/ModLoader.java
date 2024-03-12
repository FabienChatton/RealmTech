package ch.realmtech.server.newMod;

import ch.realmtech.server.newRegistry.InvalideEvaluate;
import ch.realmtech.server.newRegistry.NewRegistry;
import ch.realmtech.server.newRegistry.RegistryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModLoader {
    private final static Logger logger = LoggerFactory.getLogger(ModLoader.class);
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
                logger.warn("Invalide evaluation for {} entry. Error: {}", entry, e.getMessage());
            } catch (Exception e) {
                logger.error("Error during evaluation for {} entry. Error: {}", entry, e.getMessage(), e);
            }
        });
    }
}
