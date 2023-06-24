package ch.realmtech.game.registery.infRegistry;

public class InfRegistryEntry<T extends InfEntry> extends InfRegistry<T> {
    private final T entry;
    protected InfRegistryEntry(InfRegistry<T> parent, String name, T entry) {
        super(parent, name);
        this.entry = entry;
    }

    public T getEntry() {
        return entry;
    }
}
