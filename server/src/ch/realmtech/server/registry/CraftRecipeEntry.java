package ch.realmtech.server.registry;

import ch.realmtech.server.craft.CraftResult;

import java.util.List;
import java.util.Optional;

public abstract class CraftRecipeEntry extends Entry {
    public CraftRecipeEntry(String name) {
        super(name);
    }

    public abstract Optional<CraftResult> craft(List<ItemEntry> items);
}
