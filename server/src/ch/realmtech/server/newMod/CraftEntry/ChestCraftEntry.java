package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class ChestCraftEntry extends NewCraftPatternShape {
    public ChestCraftEntry() {
        super("ChestCraft", "realmtech.items.Chest", 1, new char[][]{
                {'p', 'p', 'p'},
                {'p', ' ', 'p'},
                {'p', 'p', 'p'}
        }, new NewPatternArgs('p', "realmtech.items.Plank"));
    }
}
