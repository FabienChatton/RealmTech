package ch.realmtech.server.newRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NewRegistry<T extends NewEntry> {
    final NewRegistry<? extends T> parentRegistry;
    final List<NewRegistry<? extends T>> childRegistries;
    final List<T> entries;
    private final String name;
    private final List<String> tags;

    private NewRegistry(NewRegistry<T> parentRegistry, String name, String... tags) {
        if (name.length() != 1 && name.contains("."))
            throw new IllegalArgumentException("Registry " + name + " can't have a \".\" in his name");
        Arrays.stream(tags).filter((tag) -> tag.contains(".")).findFirst().ifPresent((illegalTag) -> {
            throw new IllegalArgumentException("Tag" + illegalTag + " can't hava a \".\"");
        });
        this.parentRegistry = parentRegistry;
        this.name = name;
        this.tags = new ArrayList<>(List.of(tags));
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
    public static <T extends NewEntry> NewRegistry<T> createRegistry(NewRegistry<?> parentRegistry, String name, String... tags) {
        return (NewRegistry<T>) new NewRegistry<>(parentRegistry, name, tags);
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

    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }
}
