package ch.realmtech.server.registry;

import java.util.*;
import java.util.function.Function;

public class RegistryUtils {

    public static List<Registry<?>> getParentsRegistry(Registry<?> registry) {
        if (registry.parentRegistry == null) {
            return List.of();
        } else {
            List<Registry<?>> parentsRegistry = new ArrayList<>(getParentsRegistry(registry.parentRegistry));
            parentsRegistry.add(registry);
            return parentsRegistry;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entry> Optional<T> findEntry(Registry<?> registry, String query) {
        Optional<T> newEntry;
        // fqrn search
        if (query.contains(".")) {
            Optional<? extends Entry> fqrnEntry;
            if (!query.startsWith(".")) {
                fqrnEntry = searchFqrnEntry(registry, "." + query);
            } else {
                fqrnEntry = searchFqrnEntry(registry, query);
            }
            newEntry = (Optional<T>) fqrnEntry;
        } else {
            // entry search
            newEntry = (Optional<T>) flatEntry(registry).stream()
                    .filter((entry) -> entry.getName().equals(query))
                    .findFirst();
        }
        return newEntry;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entry> Optional<T> findEntry(Registry<?> registry, int entryHash) {
        return (Optional<T>) flatEntry(registry).stream().filter((entry) -> entry.getId() == entryHash).findFirst();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entry> T findEntryUnsafe(Registry<?> registry, int id) throws IllegalRegistryId {
        Optional<? extends Entry> res = flatEntry(registry).stream().filter((entry) -> entry.getId() == id).findFirst();
        if (res.isPresent()) {
            return (T) res.get();
        } else {
            throw new IllegalRegistryId("Id:" + id + " not found");
        }
    }

    public static List<? extends Entry> flatEntry(Registry<?> registry) {
        ArrayList<Entry> entries = new ArrayList<>();
        flatEntry(registry, entries);
        return entries;
    }

    private static void flatEntry(Registry<?> registry, List<Entry> entries) {
        entries.addAll(registry.getEntries());
        registry.childRegistries.forEach((childRegistry) -> flatEntry(childRegistry, entries));
    }


    public static List<? extends Entry> findEntries(Registry<?> rootRegistry, String tag) {
        return flatEntry(rootRegistry).stream().filter((entry) -> entry.parentRegistry.getTags().contains(tag.substring(1))).toList();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entry> List<T> flatEntry(Registry<?> registry, Class<T> clazz) {
        return (List<T>) flatEntry(registry).stream().filter(clazz::isInstance).toList();
    }

    public static <T extends Entry> Optional<T> findEntry(Registry<?> registry, Class<T> entryClazz) {
        List<T> entries = flatEntry(registry, entryClazz);
        if (entries.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(entries.get(0));
        }
    }

    public static <T extends Entry> T findEntryOrThrow(Registry<?> registry, Class<T> entryClazz) {
        List<T> entries = flatEntry(registry, entryClazz);
        if (entries.isEmpty()) {
            throw new NoSuchElementException("Can not find " + entryClazz + " entry.");
        } else {
            return entries.get(0);
        }
    }

    public static List<Registry<?>> flatRegistries(Registry<?> registry) {
        if (registry.childRegistries.isEmpty()) {
            return List.of(registry);
        } else {
            return registry.childRegistries.stream()
                    .map(RegistryUtils::flatRegistries)
                    .flatMap(Collection::stream)
                    .toList();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entry> T evaluateSafe(Registry<?> registry, String query, Class<T> entryClass) throws InvalideEvaluate {
        Optional<Entry> entryFind = findEntry(registry, query);
        if (entryFind.isEmpty()) {
            throw new InvalideEvaluate("Can not find " + query + " entry.");
        } else {
            Entry entry = entryFind.get();
            if (!entryClass.isInstance(entry)) {
                throw new InvalideEvaluate(query + " not a instanceof " + entryClass.getSimpleName());
            } else {
                return (T) entry;
            }
        }
    }

    public static <T extends Entry> T evaluateSafe(Registry<?> registry, Class<T> entryClass) throws InvalideEvaluate {
        return findEntry(registry, entryClass).orElseThrow(() -> new InvalideEvaluate("Can not find " + entryClass.toString() + " entry."));
    }

    private static Optional<? extends Entry> searchFqrnEntry(Registry<? extends Entry> registry, String fqrn) {
        String[] names = fqrn.split("\\.", 2);
        if (registry.getName().equals(names[0])) {
            if (!names[1].contains(".")) {
                return registry.entries.stream()
                        .filter((entry) -> entry.getName().equals(names[1]))
                        .findFirst();
            } else {
                return registry.childRegistries.stream()
                        .filter((childRegistry) -> childRegistry.getName().equals(names[1].split("\\.")[0]))
                        .findFirst()
                        .map((childRegistry) -> searchFqrnEntry(childRegistry, names[1]))
                        .flatMap(Function.identity());
            }
        } else {
            return registry.childRegistries.stream()
                    .map((childRegistry) -> searchFqrnEntry(childRegistry, names[1]))
                    .findFirst()
                    .flatMap(Function.identity());
        }
    }

    public static Optional<Registry<?>> findRegistry(Registry<?> registry, String query) {
        if (query.contains(".")) {
            Optional<Registry<?>> fqrnRegistry;
            if (!query.startsWith(".")) {
                fqrnRegistry = searchRegistryFqrn(registry, "." + query);
            } else {
                fqrnRegistry = searchRegistryFqrn(registry, query);
            }
            return fqrnRegistry;
        } else if (query.startsWith("#")) {
            return findRegistryTag(registry, query);
        } else {
            return Optional.empty();
        }
    }

    private static Optional<Registry<?>> findRegistryTag(Registry<?> registry, String tagQuery) {
        return flatRegistries(registry).stream().filter((findRegistry) -> findRegistry.getTags().contains(tagQuery.substring(1))).findFirst();
    }

    private static Optional<Registry<?>> searchRegistryFqrn(Registry<?> registry, String fqrn) {
        String[] names = fqrn.split("\\.", 2);
        if (names.length == 1 && names[0].equals(registry.getName())) {
            return Optional.of(registry);
        } else {
            Optional<Registry<?>> newRegistry = registry.getChildRegistries().stream()
                    .filter((childRegistry) -> childRegistry.getName().equals(names[1].split("\\.")[0]))
                    .map((childRegistry) -> searchRegistryFqrn(childRegistry, names[1]))
                    .filter(Optional::isPresent)
                    .findFirst()
                    .flatMap(Function.identity());

            return newRegistry;
        }
    }
}
