package ch.realmtech.server.mod.crafts.craftingtable;

import ch.realmtech.server.craft.PatternArgs;
import ch.realmtech.server.registry.CraftPatternShape;

public class StoneShovelCraftEntry extends CraftPatternShape {
    public StoneShovelCraftEntry() {
        super("StoneShovelCraft", "realmtech.items.StoneShovel", 1, new char[][]{
                {' ', 's', ' '},
                {' ', 'a', ' '},
                {' ', 'a', ' '},
        }, new PatternArgs('s', "realmtech.items.StoneOre"), new PatternArgs('a', "realmtech.items.Stick"));
    }
}
