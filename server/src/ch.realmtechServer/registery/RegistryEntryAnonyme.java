package ch.realmtechServer.registery;

public class RegistryEntryAnonyme<T extends Entry<T>> extends InfRegistryAnonyme<T> implements AbstractRegistryEntry<T> {
    private final T entry;

    protected RegistryEntryAnonyme(InfRegistryAnonyme<T> parent, T entry) {
        super(parent);
        this.entry = entry;
    }

    @Override
    public T getEntry() {
        return entry;
    }
}
