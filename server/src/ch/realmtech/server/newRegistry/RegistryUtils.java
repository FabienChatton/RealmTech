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
                fqrnEntry = searchFqrnEntry("." + query, registry);
            } else {
                fqrnEntry = searchFqrnEntry(query, registry);
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

    private static Optional<? extends NewEntry> searchFqrnEntry(String fqrn, NewRegistry<?> registry) {
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
                        .map((childRegistry) -> searchFqrnEntry(names[1], childRegistry))
                        .flatMap(Function.identity());
            }
        } else {
            return registry.childRegistries.stream()
                    .map((childRegistry) -> searchFqrnEntry(names[1], childRegistry))
                    .findFirst()
                    .flatMap(Function.identity());
        }
    }
}
