package ch.realmtech.server.mod.crafts.craftingtable;

import ch.realmtech.server.craft.PatternArgs;
import ch.realmtech.server.registry.CraftPatternShape;

public class WrenchCraftEntry extends CraftPatternShape {
    public WrenchCraftEntry() {
        super("WrenchCraft", "realmtech.items.Wrench", 1, new char[][]{
                {'a', ' ', 'a'},
                {' ', 'a', ' '},
                {' ', 'a', ' '},
        }, new PatternArgs('a', "realmtech.items.IronIngot"));
    }
}
