package ch.realmtech.server.craft;

import ch.realmtech.server.registery.CraftingRecipeEntry;
import ch.realmtech.server.registery.Registry;

public abstract class FurnacePattern implements CraftingRecipeEntry {
    protected Registry<CraftingRecipeEntry> registry;

    @Override
    public void setRegistry(Registry<CraftingRecipeEntry> registry) {
        this.registry = registry;
    }
}
