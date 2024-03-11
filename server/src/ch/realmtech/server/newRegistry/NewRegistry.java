package ch.realmtech.server.newRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NewRegistry<T extends NewEntry> {
    final NewRegistry<? extends NewEntry> parentRegistry;
    final List<NewRegistry<? extends NewEntry>> childRegistries;
    final List<T> entries;
    private final String name;

    private NewRegistry(NewRegistry<?> parentRegistry, String name) {
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

    public static <T extends NewEntry> NewRegistry<T> createRegistry(NewRegistry<?> parentRegistry, String name) {
        return new NewRegistry<>(parentRegistry, name);
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

    public List<NewRegistry<?>> getChildRegistries() {
        return Collections.unmodifiableList(childRegistries);
    }

    public List<T> getEntries() {
        return Collections.unmodifiableList(entries);
    }
}
