package ch.realmtech.game.registery;

import java.util.HashMap;

public class Registry<E extends RegistryEntry> extends HashMap<String, E>{
    private final String modId;
    private Registry(String modId) {
        this.modId = modId;
    }

    public static <T extends RegistryEntry> Registry<T> create(String modId){
        return new Registry<>(modId);
    }
}
