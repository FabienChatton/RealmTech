package ch.realmtech.game.registery.infRegistry;

public class InfRegistryEntryAnonyme<T extends InfEntry> extends InfRegistryAnonyme<T>  {
    private final T entry;
    protected InfRegistryEntryAnonyme(InfRegistryAnonyme<T> parent, T entry) {
        super(parent);
        this.entry = entry;
    }

    public T getEntry() {
        return entry;
    }
}
