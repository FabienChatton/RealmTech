package ch.realmtech.server.mod.crafts.craftingtable;

import ch.realmtech.server.craft.PatternArgs;
import ch.realmtech.server.registry.CraftPatternShape;

public class WoodenShovelCraftEntry extends CraftPatternShape {
    public WoodenShovelCraftEntry() {
        super("WoodenShovelCraft", "realmtech.items.WoodenShovel", 1, new char[][]{
                {' ', 'a', ' '},
                {' ', 'b', ' '},
                {' ', 'b', ' '}
        }, new PatternArgs('a', "realmtech.items.Plank"), new PatternArgs('b', "realmtech.items.Stick"));
    }
}
