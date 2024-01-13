package ch.realmtech.server.registery;

import java.util.List;

public interface InfRegistryImmutable<T extends Entry<T>> {
    List<AbstractRegistryEntry<T>> getEnfants();
}
