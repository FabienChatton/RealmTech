package ch.realmtechServer.registery;

import java.util.ArrayList;
import java.util.List;

public class InfRegistryAnonyme<T extends Entry<T>> {
    private final InfRegistryAnonyme<T> parent;
    private final List<RegistryEntryAnonyme<T>> enfants;

    protected InfRegistryAnonyme(InfRegistryAnonyme<T> parent) {
        this.parent = parent;
        enfants = new ArrayList<>();
    }

    public List<RegistryEntryAnonyme<T>> getEnfants() {
        return enfants;
    }

    public void add(T entry) {
        enfants.add(new RegistryEntryAnonyme<>(this, entry));
    }

    public static <T extends Entry<T>> InfRegistryAnonyme<T> create() {
        return new InfRegistryAnonyme<>(null);
    }

    public static <T extends Entry<T>> InfRegistryAnonyme<T> create(InfRegistryAnonyme<T> parent) {
        return new InfRegistryAnonyme<>(parent);
    }
}
