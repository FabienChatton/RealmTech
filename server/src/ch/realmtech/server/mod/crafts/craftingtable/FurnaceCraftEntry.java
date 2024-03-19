package ch.realmtech.server.mod.crafts.craftingtable;

import ch.realmtech.server.craft.PatternArgs;
import ch.realmtech.server.registry.CraftPatternShape;

public class FurnaceCraftEntry extends CraftPatternShape {
    public FurnaceCraftEntry() {
        super("FurnaceCraft", "realmtech.items.Furnace", 1, new char[][]{
                {'p', 'p', 'p'},
                {'p', ' ', 'p'},
                {'p', 'p', 'p'}
        }, new PatternArgs('p', "realmtech.items.StoneOre"));
    }
}
