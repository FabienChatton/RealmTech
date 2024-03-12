package ch.realmtech.server.registery;

import java.util.List;

@Deprecated
public interface InfRegistryAnonymeImmutable<T extends Entry<T>> {
    List<RegistryEntryAnonyme<T>> getEnfants();
}
