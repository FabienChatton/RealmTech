package ch.realmtech.game.registery;

import java.util.Optional;

public interface Entry {
    default <T extends Entry> Optional<RegistryEntry<T>> findRegistryEntry(Registry<T> registry) {
        return registry.getEnfants().stream()
                .filter(tRegistryEntry -> tRegistryEntry.getEntry() == this)
                .findFirst();
    }
}
