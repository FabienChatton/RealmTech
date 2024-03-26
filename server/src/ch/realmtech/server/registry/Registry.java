package ch.realmtech.server.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Organise the content of RealmTech. Contains sub-registry and {@link Entry}.
 * Every registry need to have a parent registry (expect for root registry).
 * {@link RegistryUtils} is available to do some operation on registry.
 *
 * @param <T> Optional registry type. If your went to add entry to this registry, type this registry with the corresponding entry type. If you
 *            don't want to add entry to this registry, type the registry generic this <code>?</code>
 */
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

    /**
     * Create a root registry. Only use by RealmTech mod loader.
     * @return a newly root registry.
     */
    public static Registry<?> createRoot() {
        return new Registry<>(null, ".");
    }

    /**
     * Create a sub-registry.
     * @param parentRegistry The parent registry.
     * @param name The name of the newly sub-registry.
     * @param tags optional tags for entries.
     * @return The newly created sub-registry.
     * @param <T> Optional registry type. If your went to add entry to this registry, type this registry with the corresponding entry type. If you
     *           don't want to add entry to this registry, type the registry generic this <code>?</code>
     */
    @SuppressWarnings("unchecked")
    public static <T extends Entry> Registry<T> createRegistry(Registry<?> parentRegistry, String name, String... tags) {
        return (Registry<T>) new Registry<>(parentRegistry, name, tags);
    }

    /**
     * Add entry to this registry.
     * @param entry The entry to be added.
     */
    public void addEntry(T entry) {
        entries.add(entry);
        entry.setParentRegistry(this);
    }

    /**
     * Get the fqrn to this registry.
     * The fqrn is used to retrieve precisely a registry or entry.
     * @return The fqrn of this registry.
     */
    public String toFqrn() {
        return RegistryUtils.getParentsRegistry(this).stream().map(Registry::getName).collect(Collectors.joining("."));
    }

    /**
     * To string is replaced by to fqrn
     * @return the fqrn.
     */
    @Override
    public String toString() {
        return toFqrn();
    }

    /**
     * Get name of this registry.
     * @return The name of this registry.
     */
    public String getName() {
        return name;
    }

    /**
     * Get a list of unmodifiable child registries.
     * @return unmodifiable list of child registries.
     */
    public List<Registry<? extends T>> getChildRegistries() {
        return Collections.unmodifiableList(childRegistries);
    }

    /**
     * Get a list of unmodifiable entries.
     * @return unmodifiable list of entries.
     */
    public List<T> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Get a list of unmodifiable tags.
     * @return unmodifiable list of tags.
     */
    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }
}
