package ch.realmtechServer.registery;

import java.util.Optional;

public interface Entry<T extends Entry<T>> {
    default Optional<RegistryEntry<T>> findRegistryEntry(Registry<T> registry) {
        return registry.getEnfants().stream()
                .filter(tRegistryEntry -> tRegistryEntry.getEntry() == this)
                .findFirst();
    }

    default String findRegistryEntryToString(Registry<T> registry) {
        return findRegistryEntry(registry)
                .map(Registry::getID).orElse("not find in this registry");

    }

    void setRegistry(Registry<T> registry);
}
