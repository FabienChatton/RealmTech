package ch.realmtech.game.registery.infRegistry;

import java.util.ArrayList;
import java.util.List;

public class InfRegistryAnonyme<T extends InfEntry> {
    private final InfRegistryAnonyme<T> parent;
    private final List<InfRegistryEntryAnonyme<T>> enfants;

    protected InfRegistryAnonyme(InfRegistryAnonyme<T> parent) {
        this.parent = parent;
        enfants = new ArrayList<>();
    }

    public List<InfRegistryEntryAnonyme<T>> getEnfants() {
        return enfants;
    }

    public void add(T entry) {
        enfants.add(new InfRegistryEntryAnonyme<>(this, entry));
    }
    public static <T extends InfEntry> InfRegistryAnonyme<T> create() {
        return new InfRegistryAnonyme<>(null);
    }
    public static <T extends InfEntry> InfRegistryAnonyme<T> create(InfRegistryAnonyme<T> parent) {
        return new InfRegistryAnonyme<>(parent);
    }
}
