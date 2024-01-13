package ch.realmtech.server.registery;

public class RegistryEntryAnonyme<T extends Entry<T>> implements AbstractRegistryEntry<T> {
    private final T entry;

    protected RegistryEntryAnonyme(InfRegistryAnonyme<T> parent, T entry) {
        this.entry = entry;
    }

    @Override
    public T getEntry() {
        return entry;
    }
}
