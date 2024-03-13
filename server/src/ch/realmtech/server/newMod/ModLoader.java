package ch.realmtech.server.newMod;

import ch.realmtech.server.newRegistry.InvalideEvaluate;
import ch.realmtech.server.newRegistry.NewEntry;
import ch.realmtech.server.newRegistry.NewRegistry;
import ch.realmtech.server.newRegistry.RegistryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ModLoader {
    private final static Logger logger = LoggerFactory.getLogger(ModLoader.class);
    private final NewRegistry<?> rootRegistry;

    public ModLoader(NewRegistry<?> rootRegistry) {
        this.rootRegistry = rootRegistry;
    }

    public void initializeCoreMod() {
        long t1 = System.currentTimeMillis();
        NewRealmTechCoreMod newRealmTechCoreMod = new NewRealmTechCoreMod();
        String coreModId = newRealmTechCoreMod.getModId();
        NewRegistry<?> coreModRegistry = NewRegistry.createRegistry(rootRegistry, coreModId);

        newRealmTechCoreMod.initializeModRegistry(coreModRegistry);

        List<? extends NewEntry> entries = RegistryUtils.flatEntry(rootRegistry);

        try {
            evaluateAfter(rootRegistry, new LinkedList<>(entries));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        logger.info("All mods are initialized in {}s", (System.currentTimeMillis() - t1) / 1000f);
    }

    private void evaluateAfter(NewRegistry<?> rootRegistry, Queue<NewEntry> entriesToEvaluate) throws NoSuchMethodException {
        while (!entriesToEvaluate.isEmpty()) {
            NewEntry entry = entriesToEvaluate.poll();
            try {
                EvaluateAfter evaluateAfter = entry.getClass().getMethod("evaluate", NewRegistry.class).getAnnotation(EvaluateAfter.class);
                if (evaluateAfter != null) {
                    if (RegistryUtils.findRegistry(rootRegistry, evaluateAfter.value()).isEmpty() && RegistryUtils.findEntry(rootRegistry, evaluateAfter.value()).isEmpty()) {
                        throw new InvalideEvaluate("Can not find " + evaluateAfter.value() + " registry or entry");
                    }
                    boolean registryEvaluated = RegistryUtils.findRegistry(rootRegistry, evaluateAfter.value())
                            .map((findRegistry) -> findRegistry.getEntries().stream().allMatch(NewEntry::isEvaluated)).orElse(false);
                    boolean entryEvaluated = RegistryUtils.findEntry(rootRegistry, evaluateAfter.value())
                            .map(NewEntry::isEvaluated).orElse(false);
                    if (registryEvaluated || entryEvaluated) {
                        entry.evaluate(rootRegistry);
                    } else {
                        entriesToEvaluate.add(entry);
                    }
                } else {
                    entry.evaluate(rootRegistry);
                }
            } catch (InvalideEvaluate e) {
                logger.warn("Invalide evaluation for {} entry. Error: {}", entry, e.getMessage());
            } catch (Exception e) {
                logger.error("Error during evaluation for {} entry. Error: {}", entry, e.getMessage(), e);
            }
        }
    }
}
