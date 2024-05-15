package ch.realmtech.server.mod.crafts.craftingtable;

import ch.realmtech.server.craft.PatternArgs;
import ch.realmtech.server.registry.CraftPatternShape;

public class StoneSwordCraftEntry extends CraftPatternShape {
    public StoneSwordCraftEntry() {
        super("StoneSwordCraft", "realmtech.items.StoneSword", 1, new char[][]{
                {' ', 'b', ' '},
                {' ', 'b', ' '},
                {' ', 's', ' '},
        }, new PatternArgs('b', "realmtech.items.StoneOre"), new PatternArgs('s', "realmtech.items.Stick"));
    }
}
