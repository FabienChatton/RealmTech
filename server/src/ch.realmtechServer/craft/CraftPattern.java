package ch.realmtechServer.craft;

import ch.realmtechServer.registery.CraftingRecipeEntry;
import ch.realmtechServer.registery.Registry;

public abstract class CraftPattern implements CraftingRecipeEntry {
    protected Registry<CraftingRecipeEntry> registry;

    @Override
    public void setRegistry(Registry<CraftingRecipeEntry> registry) {
        this.registry = registry;
    }
}
