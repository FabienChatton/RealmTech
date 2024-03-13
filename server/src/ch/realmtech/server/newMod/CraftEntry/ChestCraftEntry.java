package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class ChestCraftEntry extends NewCraftPatternShape {
    public ChestCraftEntry() {
        super("chestCraft", "realmtech.items.chest", 1, new char[][]{
                {'p', 'p', 'p'},
                {'p', ' ', 'p'},
                {'p', 'p', 'p'}
        }, new NewPatternArgs('p', "realmtech.items.plank"));
    }
}
