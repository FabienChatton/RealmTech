package ch.realmtechCommuns.craft;

import ch.realmtechCommuns.registery.CraftingRecipeEntry;
import ch.realmtechCommuns.registery.Registry;

public abstract class FurnacePattern implements CraftingRecipeEntry {
    protected Registry<CraftingRecipeEntry> registry;

    @Override
    public void setRegistry(Registry<CraftingRecipeEntry> registry) {
        this.registry = registry;
    }
}
