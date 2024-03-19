package ch.realmtech.server.mod.crafts.craftingtable;

import ch.realmtech.server.craft.PatternArgs;
import ch.realmtech.server.registry.CraftPatternShape;

public class CraftingTableCraftEntry extends CraftPatternShape {
    public CraftingTableCraftEntry() {
        super("CraftingTableCraft", "realmtech.items.CraftingTable", 1, new char[][]{
                {'p', 'p'},
                {'p', 'p'}
        }, new PatternArgs('p', "realmtech.items.Plank"));
    }
}
