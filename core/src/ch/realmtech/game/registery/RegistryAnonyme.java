package ch.realmtech.game.registery;

import com.badlogic.gdx.utils.Array;

public class RegistryAnonyme<TEntry> extends Array<TEntry> {
    private final String modId;

    private RegistryAnonyme(String modId) {
        this.modId = modId;
    }

    public static RegistryAnonyme<CraftingRecipeEntry> create(String modId) {
        return new RegistryAnonyme<>(modId);
    }
}
