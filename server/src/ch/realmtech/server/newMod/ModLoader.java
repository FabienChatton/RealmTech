package ch.realmtech.server.newMod;

import ch.realmtech.server.newRegistry.InvalideEvaluate;
import ch.realmtech.server.newRegistry.NewEntry;
import ch.realmtech.server.newRegistry.NewRegistry;
import ch.realmtech.server.newRegistry.RegistryUtils;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.StreamSupport;

public class ModLoader {
    private final static Logger logger = LoggerFactory.getLogger(ModLoader.class);
    private final NewRegistry<?> rootRegistry;
    private boolean isFail;

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

        List<NewEntry> entrySort = sortEntryDependency(rootRegistry, entries);

        for (NewEntry entry : entrySort) {
            try {
                entry.evaluate(rootRegistry);
                entry.setEvaluated(true);
            } catch (InvalideEvaluate e) {
                logger.warn("Invalide evaluation for {} entry. Error: {}", entry, e.getMessage());
                isFail = true;
            } catch (Exception e) {
                logger.error("Error during evaluation for {} entry. Error: {}", entry, e.getMessage(), e);
                isFail = true;
            }
        }

        if (isFail) {
            throw new RuntimeException("Can not launch game if mods are not loaded correctly");
        }

        logger.info("All mods are initialized in {}s", (System.currentTimeMillis() - t1) / 1000f);
    }

    private List<NewEntry> sortEntryDependency(NewRegistry<?> rootRegistry, List<? extends NewEntry> entries) {
        DirectedAcyclicGraph<NewEntry, DefaultEdge> graph = new DirectedAcyclicGraph<>(DefaultEdge.class);
        for (NewEntry entry : entries) {
            graph.addVertex(entry);
        }
        for (NewEntry entry : entries) {
            try {
                EvaluateAfter evaluateAfter = entry.getClass().getMethod("evaluate", NewRegistry.class).getAnnotation(EvaluateAfter.class);
                if (evaluateAfter != null) {
                    for (String evaluateAfterQuery : evaluateAfter.value()) {
                        List<NewEntry> entriesDependent = processEvaluateAnnotation(rootRegistry, evaluateAfterQuery);
                        for (NewEntry entryDependent : entriesDependent) {
                            try {
                                graph.addEdge(entryDependent, entry);
                            } catch (IllegalArgumentException e) {
                                throw new InvalideEvaluate("Circular dependency, can not depend on " + evaluateAfterQuery);
                            }
                        }
                    }
                }
                EvaluateBefore evaluateBefore = entry.getClass().getMethod("evaluate", NewRegistry.class).getAnnotation(EvaluateBefore.class);
                if (evaluateBefore != null) {
                    for (String evaluateBeforeQuery : evaluateBefore.value()) {
                        List<NewEntry> entriesDependent = processEvaluateAnnotation(rootRegistry, evaluateBeforeQuery);
                        for (NewEntry entryDependent : entriesDependent) {
                            try {
                                graph.addEdge(entry, entryDependent);
                            } catch (IllegalArgumentException e) {
                                throw new InvalideEvaluate("Circular dependency, can not depend on " + evaluateBeforeQuery);
                            }
                        }
                    }
                }

            } catch (InvalideEvaluate e) {
                logger.warn("Invalide dependency sort for {} entry. Error: {}", entry, e.getMessage());
                isFail = true;
            } catch (Exception e) {
                logger.error("Error during dependency sort for {} entry. Error: {}", entry, e.getMessage(), e);
                isFail = true;
            }
        }

        return StreamSupport.stream(graph.spliterator(), false).toList();
    }

    @SuppressWarnings("unchecked")
    private List<NewEntry> processEvaluateAnnotation(NewRegistry<?> rootRegistry, String evaluateQuery) throws InvalideEvaluate {
        return RegistryUtils.findRegistry(rootRegistry, evaluateQuery)
                .map((registry) -> (List<NewEntry>) registry.getEntries())
                .or(() -> RegistryUtils.findEntry(rootRegistry, evaluateQuery).map(List::of))
                .orElseThrow(() -> new InvalideEvaluate("Can not find " + evaluateQuery + " registry or entry"));
    }
}
