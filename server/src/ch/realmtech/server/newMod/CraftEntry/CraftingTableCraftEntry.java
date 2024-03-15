package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class CraftingTableCraftEntry extends NewCraftPatternShape {
    public CraftingTableCraftEntry() {
        super("CraftingTableCraft", "realmtech.items.CraftingTable", 1, new char[][]{
                {'p', 'p'},
                {'p', 'p'}
        }, new NewPatternArgs('p', "realmtech.items.Plank"));
    }
}
