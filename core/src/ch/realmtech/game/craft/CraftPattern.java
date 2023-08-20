package ch.realmtech.game.craft;

import ch.realmtech.game.registery.CraftingRecipeEntry;
import ch.realmtech.game.registery.Registry;

public abstract class CraftPattern implements CraftingRecipeEntry {
    protected Registry<CraftingRecipeEntry> registry;

    @Override
    public void setRegistry(Registry<CraftingRecipeEntry> registry) {
        this.registry = registry;
    }
}
