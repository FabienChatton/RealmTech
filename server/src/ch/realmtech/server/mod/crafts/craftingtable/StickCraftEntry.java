package ch.realmtech.server.mod.crafts.craftingtable;

import ch.realmtech.server.craft.PatternArgs;
import ch.realmtech.server.registry.CraftPatternShape;

public class StickCraftEntry extends CraftPatternShape {
    public StickCraftEntry() {
        super("StickCraft", "realmtech.items.Stick", 2, new char[][]{
                {'p'},
                {'p'}
        }, new PatternArgs('p', "realmtech.items.Plank"));
    }
}
