package ch.realmtech.server.registery;

public class RegistryEntry<T extends Entry<T>> extends Registry<T> implements AbstractRegistryEntry<T> {
    private final T entry;

    protected RegistryEntry(Registry<T> parent, String name, T entry) {
        super(parent, name);
        this.entry = entry;
    }

    @Override
    public T getEntry() {
        return entry;
    }
}
