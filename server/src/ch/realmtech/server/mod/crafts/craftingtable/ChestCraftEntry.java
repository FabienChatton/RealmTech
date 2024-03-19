package ch.realmtech.server.mod.crafts.craftingtable;

import ch.realmtech.server.craft.PatternArgs;
import ch.realmtech.server.registry.CraftPatternShape;

public class ChestCraftEntry extends CraftPatternShape {
    public ChestCraftEntry() {
        super("ChestCraft", "realmtech.items.Chest", 1, new char[][]{
                {'p', 'p', 'p'},
                {'p', ' ', 'p'},
                {'p', 'p', 'p'}
        }, new PatternArgs('p', "realmtech.items.Plank"));
    }
}
