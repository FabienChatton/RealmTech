package ch.realmtech.server.newRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class RegistryUtils {

    public static List<NewRegistry<?>> getParentsRegistry(NewRegistry<?> registry) {
        if (registry.parentRegistry == null) {
            return List.of();
        } else {
            List<NewRegistry<?>> parentsRegistry = new ArrayList<>(getParentsRegistry(registry.parentRegistry));
            parentsRegistry.add(registry);
            return parentsRegistry;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends NewEntry> Optional<T> findEntry(NewRegistry<?> registry, String query) {
        Optional<T> newEntry;
        // fqrn search
        if (query.contains(".")) {
            Optional<? extends NewEntry> fqrnEntry;
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
    public static <T extends NewEntry> Optional<T> findEntry(NewRegistry<?> registry, int entryHash) {
        return (Optional<T>) flatEntry(registry).stream().filter((entry) -> entry.getId() == entryHash).findFirst();
    }

    public static <T extends NewEntry> T findEntryUnsafe(NewRegistry<?> registry, int entryHash) throws IllegalRegistryId {
        Optional<? extends NewEntry> res = flatEntry(registry).stream().filter((entry) -> entry.getId() == entryHash).findFirst();
        if (res.isPresent()) {
            return (T) res.get();
        } else {
            throw new IllegalRegistryId("Id:" + entryHash + " not found");
        }
    }

    public static List<? extends NewEntry> flatEntry(NewRegistry<?> registry) {
        if (registry.childRegistries.isEmpty()) {
            return registry.entries;
        } else {
            return registry.childRegistries.stream()
                    .map(RegistryUtils::flatEntry)
                    .flatMap(Collection::stream)
                    .toList();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends NewEntry> T evaluateSafe(NewRegistry<?> registry, String query, Class<T> entryClass) throws InvalideEvaluate {
        Optional<NewEntry> entryFind = findEntry(registry, query);
        if (entryFind.isEmpty()) {
            throw new InvalideEvaluate("Can not find " + query + " entry.");
        } else {
            NewEntry newEntry = entryFind.get();
            if (!entryClass.isInstance(newEntry)) {
                throw new InvalideEvaluate(query + " not a instanceof " + entryClass.getSimpleName());
            } else {
                return (T) newEntry;
            }
        }
    }

    private static Optional<? extends NewEntry> searchFqrnEntry(NewRegistry<? extends NewEntry> registry, String fqrn) {
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

    public static Optional<NewRegistry<?>> findRegistry(NewRegistry<?> registry, String query) {
        if (query.contains(".")) {
            Optional<NewRegistry<?>> fqrnRegistry;
            if (!query.startsWith(".")) {
                fqrnRegistry = searchRegistryFqrn(registry, "." + query);
            } else {
                fqrnRegistry = searchRegistryFqrn(registry, query);
            }
            return fqrnRegistry;
        } else {
            return Optional.empty();
        }
    }

    private static Optional<NewRegistry<?>> searchRegistryFqrn(NewRegistry<?> registry, String fqrn) {
        String[] names = fqrn.split("\\.", 2);
        if (names.length == 1 && names[0].equals(registry.getName())) {
            return Optional.of(registry);
        } else {
            Optional<NewRegistry<?>> newRegistry = registry.getChildRegistries().stream()
                    .filter((childRegistry) -> childRegistry.getName().equals(names[1].split("\\.")[0]))
                    .map((childRegistry) -> searchRegistryFqrn(childRegistry, names[1]))
                    .filter(Optional::isPresent)
                    .findFirst()
                    .flatMap(Function.identity());

            return newRegistry;
        }
    }
}
