package ch.realmtech.game.registery.infRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InfRegistry<T extends InfEntry> {
    private final InfRegistry<T> parent;
    private final String name;
    private final List<InfRegistryEntry<T>> enfants;

    protected InfRegistry(InfRegistry<T> parent, String name) {
        this.parent = parent;
        this.name = name;
        enfants = new ArrayList<>();
        if (!name.matches("^[a-zA-Z]+$")) throw new IllegalArgumentException("le nom du registre doit contenir uniquement des lettres entre a et z en minuscule ou majuscule" + getID());
    }

    public static <T extends InfEntry> InfRegistry<T> create(String name) {
        return new InfRegistry<>(null, name);
    }
    public static <T extends InfEntry> InfRegistry<T> create(String name, InfRegistry<T> parent) {
        return new InfRegistry<>(parent, name);
    }
    public void add(String name, T registryEntry){
        enfants.add(new InfRegistryEntry<>(this, name, registryEntry));
    }

    public String getID() {
        if (parent != null) {
            return parent.getID() + "." + name;
        } else {
            return name;
        }
    }

    public InfRegistryEntry<T> get(String id) {
        return getEnfants().get(getEnfantsId().indexOf(id));
    }

    public List<InfRegistryEntry<T>> getEnfants() {
        return enfants;
    }

    public List<String> getEnfantsId() {
        return enfants.stream().map(InfRegistry::getID).collect(Collectors.toList());
    }
}
