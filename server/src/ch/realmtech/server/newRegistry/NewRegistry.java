package ch.realmtech.server.newRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NewRegistry<T extends NewEntry> {
    final NewRegistry<? extends T> parentRegistry;
    final List<NewRegistry<? extends T>> childRegistries;
    final List<T> entries;
    private final String name;

    private NewRegistry(NewRegistry<T> parentRegistry, String name) {
        this.parentRegistry = parentRegistry;
        this.name = name;
        entries = new ArrayList<>();
        childRegistries = new ArrayList<>();

        if (parentRegistry != null) {
            parentRegistry.childRegistries.add(this);
        }
    }

    public static NewRegistry<?> createRoot() {
        return new NewRegistry<>(null, ".");
    }

    @SuppressWarnings("unchecked")
    public static <T extends NewEntry> NewRegistry<T> createRegistry(NewRegistry<?> parentRegistry, String name) {
        return (NewRegistry<T>) new NewRegistry<>(parentRegistry, name);
    }

    public void addEntry(T entry) {
        entries.add(entry);
        entry.setParentRegistry(this);
    }

    public String toFqrn() {
        return RegistryUtils.getParentsRegistry(this).stream().map(NewRegistry::getName).collect(Collectors.joining("."));
    }

    public String getName() {
        return name;
    }

    public List<NewRegistry<? extends T>> getChildRegistries() {
        return Collections.unmodifiableList(childRegistries);
    }

    public List<T> getEntries() {
        return Collections.unmodifiableList(entries);
    }
}
