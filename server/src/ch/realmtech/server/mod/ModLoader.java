package ch.realmtech.server.mod;

import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.registry.Entry;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.Registry;
import ch.realmtech.server.registry.RegistryUtils;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.StreamSupport;

public class ModLoader {
    private final static Logger logger = LoggerFactory.getLogger(ModLoader.class);
    private final Context context;
    private final Registry<?> rootRegistry;
    private boolean isFail;

    public ModLoader(Context context, Registry<?> rootRegistry) {
        this.context = context;
        this.rootRegistry = rootRegistry;
    }

    public void initializeCoreMod() {
        long t1 = System.currentTimeMillis();
        RealmTechCoreMod realmTechCoreMod = new RealmTechCoreMod();
        String coreModId = realmTechCoreMod.getModId();
        Registry<?> coreModRegistry = Registry.createRegistry(rootRegistry, coreModId);

        realmTechCoreMod.initializeModRegistry(coreModRegistry, context);
        checkName(rootRegistry);

        List<? extends Entry> entries = RegistryUtils.flatEntry(rootRegistry);
        List<Entry> entrySort = sortEntryDependency(rootRegistry, entries);

        for (Entry entry : entrySort) {
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

    private List<Entry> sortEntryDependency(Registry<?> rootRegistry, List<? extends Entry> entries) {
        DirectedAcyclicGraph<Entry, DefaultEdge> graph = new DirectedAcyclicGraph<>(DefaultEdge.class);
        for (Entry entry : entries) {
            graph.addVertex(entry);
        }
        for (Entry entry : entries) {
            try {
                EvaluateAfter evaluateAfter = entry.getClass().getMethod("evaluate", Registry.class).getAnnotation(EvaluateAfter.class);
                if (evaluateAfter != null) {
                    for (String evaluateAfterQuery : evaluateAfter.value()) {
                        List<Entry> entriesDependent = processEvaluateAnnotation(rootRegistry, evaluateAfterQuery);
                        for (Entry entryDependent : entriesDependent) {
                            try {
                                graph.addEdge(entryDependent, entry);
                            } catch (IllegalArgumentException e) {
                                throw new InvalideEvaluate("Circular dependency, can not depend on " + evaluateAfterQuery);
                            }
                        }
                    }
                    for (Class<? extends Entry> clazz : evaluateAfter.classes()) {
                        Entry entryDependent = RegistryUtils.findEntry(rootRegistry, clazz)
                                .orElseThrow(() -> new InvalideEvaluate("Can not find " + clazz.toString() + " entry"));
                        try {
                            graph.addEdge(entryDependent, entry);
                        } catch (IllegalArgumentException e) {
                            throw new InvalideEvaluate("Circular dependency, can not depend on " + clazz.toString());
                        }
                    }
                }
                EvaluateBefore evaluateBefore = entry.getClass().getMethod("evaluate", Registry.class).getAnnotation(EvaluateBefore.class);
                if (evaluateBefore != null) {
                    for (String evaluateBeforeQuery : evaluateBefore.value()) {
                        List<Entry> entriesDependent = processEvaluateAnnotation(rootRegistry, evaluateBeforeQuery);
                        for (Entry entryDependent : entriesDependent) {
                            try {
                                graph.addEdge(entry, entryDependent);
                            } catch (IllegalArgumentException e) {
                                throw new InvalideEvaluate("Circular dependency, can not depend on " + evaluateBeforeQuery);
                            }
                        }
                    }
                    for (Class<? extends Entry> clazz : evaluateBefore.classes()) {
                        Entry entryDependent = RegistryUtils.findEntry(rootRegistry, clazz)
                                .orElseThrow(() -> new InvalideEvaluate("Can not find " + clazz.toString() + " entry"));
                        try {
                            graph.addEdge(entry, entryDependent);
                        } catch (IllegalArgumentException e) {
                            throw new InvalideEvaluate("Circular dependency, can not depend on " + clazz.toString());
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
    private List<Entry> processEvaluateAnnotation(Registry<?> rootRegistry, String evaluateQuery) throws InvalideEvaluate {
        return RegistryUtils.findRegistry(rootRegistry, evaluateQuery)
                .map((registry) -> (List<Entry>) registry.getEntries())
                .or(() -> RegistryUtils.findEntry(rootRegistry, evaluateQuery).map(List::of))
                .orElseThrow(() -> new InvalideEvaluate("Can not find " + evaluateQuery + " registry or entry"));
    }

    private void checkName(Registry<?> rootRegistry) {
        boolean invalideEntryName = RegistryUtils.flatEntry(rootRegistry).stream()
                .filter((entry) -> !entry.getName().matches("[A-Z][a-zA-Z0-9]*"))
                .peek((invalideNameEntry) -> logger.warn("Invalide name for {} entry. Error: {}", invalideNameEntry, "Entry name must begin with upper case"))
                .map((registry) -> true)
                .findAny()
                .orElse(false);

        if (invalideEntryName) {
            isFail = true;
        }

        boolean invalideRegistryName = RegistryUtils.flatRegistries(rootRegistry).stream()
                .filter((registry) -> registry.getName().matches("[A-Z][a-zA-Z0-9]*"))
                .peek((invalideNameEntry) -> logger.warn("Invalide name for {} registry. Error: {}", invalideNameEntry, "Registry name must not begin with upper case"))
                .map((registry) -> true)
                .findAny()
                .orElse(false);

        if (invalideRegistryName) {
            isFail = true;
        }

        boolean invalideRegistryNameDot = rootRegistry.getChildRegistries().stream()
                .anyMatch((childRegistry) -> RegistryUtils.flatRegistries(childRegistry).stream()
                        .filter((registry) -> registry.getName().contains("."))
                        .peek((invalideRegistry) -> logger.warn("Invalide name for {} registry. Error: {}", invalideRegistry, "Registry name must not contains \".\""))
                        .map((registry) -> true)
                        .findAny()
                        .orElse(false)
                );

        if (invalideRegistryNameDot) {
            isFail = true;
        }

        boolean invalideTagName = rootRegistry.getChildRegistries().stream()
                .anyMatch((childRegistry) -> RegistryUtils.flatRegistries(childRegistry).stream()
                        .anyMatch((registry) -> registry.getTags().stream()
                                .filter((tag) -> tag.contains("."))
                                .peek((invalideTag) -> logger.warn("Invalide name for {} tag in registry {}. Error: {}", invalideTag, registry, "Tag name must not contains \".\""))
                                .map((tag) -> true)
                                .findAny()
                                .orElse(false))

                );

        if (invalideTagName) {
            isFail = true;
        }
    }
}
