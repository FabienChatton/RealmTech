package ch.realmtech.game.registery;

import java.util.HashMap;

public class Registry<TEntry extends RegistryEntry> extends HashMap<String, TEntry>{
    private final String modId;
    private Registry(String modId) {
        this.modId = modId;
    }

    public static <TEntry extends RegistryEntry> Registry<TEntry> create(String modId){
        return new Registry<>(modId);
    }
}
