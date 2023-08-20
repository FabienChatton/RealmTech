package ch.realmtech.game.registery;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Registry<T extends Entry<T>> {
    private final Registry<T> parent;
    private final String name;
    private final List<RegistryEntry<T>> enfants;

    protected Registry(Registry<T> parent, String name) {
        this.parent = parent;
        this.name = name;
        enfants = new ArrayList<>();
        if (!name.matches("^[a-zA-Z]+$"))
            throw new IllegalArgumentException("le nom du registre doit contenir uniquement des lettres entre a et z en minuscule ou majuscule " + getID());
    }

    public static <T extends Entry<T>> Registry<T> create(String name) {
        return new Registry<>(null, name);
    }

    public static <T extends Entry<T>> Registry<T> create(String name, Registry<T> parent) {
        return new Registry<>(parent, name);
    }

    public T add(String name, T registryEntry) {
        enfants.add(new RegistryEntry<>(this, name, registryEntry));
        registryEntry.setRegistry(this);
        return registryEntry;
    }

    public String getID() {
        if (parent != null) {
            return parent.getID() + "." + name;
        } else {
            return name;
        }
    }

    public RegistryEntry<T> get(String id) {
        return getEnfants().get(getEnfantsId().indexOf(id));
    }

    public List<RegistryEntry<T>> getEnfants() {
        return enfants;
    }

    public List<String> getEnfantsId() {
        return enfants.stream().map(Registry::getID).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public int getHashID() {
        return CellRegisterEntry.hashString(getID());
    }

    @Override
    public String toString() {
        return "name: " + name + " " +
                "hash(byte): " + (CellRegisterEntry.hashString(getID()));
    }
}
