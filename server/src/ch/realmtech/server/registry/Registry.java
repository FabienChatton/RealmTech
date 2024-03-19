package ch.realmtech.server.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Registry<T extends Entry> {
    final Registry<? extends T> parentRegistry;
    final List<Registry<? extends T>> childRegistries;
    final List<T> entries;
    private final String name;
    private final List<String> tags;

    private Registry(Registry<T> parentRegistry, String name, String... tags) {
        this.parentRegistry = parentRegistry;
        this.name = name;
        this.tags = new ArrayList<>(List.of(tags));
        entries = new ArrayList<>();
        childRegistries = new ArrayList<>();

        if (parentRegistry != null) {
            parentRegistry.childRegistries.add(this);
        }
    }

    public static Registry<?> createRoot() {
        return new Registry<>(null, ".");
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entry> Registry<T> createRegistry(Registry<?> parentRegistry, String name, String... tags) {
        return (Registry<T>) new Registry<>(parentRegistry, name, tags);
    }

    public void addEntry(T entry) {
        entries.add(entry);
        entry.setParentRegistry(this);
    }

    public String toFqrn() {
        return RegistryUtils.getParentsRegistry(this).stream().map(Registry::getName).collect(Collectors.joining("."));
    }

    @Override
    public String toString() {
        return toFqrn();
    }

    public String getName() {
        return name;
    }

    public List<Registry<? extends T>> getChildRegistries() {
        return Collections.unmodifiableList(childRegistries);
    }

    public List<T> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }
}
