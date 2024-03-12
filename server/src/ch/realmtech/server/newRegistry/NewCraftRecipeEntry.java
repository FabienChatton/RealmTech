package ch.realmtech.server.newRegistry;

import ch.realmtech.server.newCraft.NewCraftResult;

import java.util.List;
import java.util.Optional;

public abstract class NewCraftRecipeEntry extends NewEntry {
    public NewCraftRecipeEntry(String name) {
        super(name);
    }

    public abstract Optional<NewCraftResult> craft(List<NewItemEntry> items);
}
