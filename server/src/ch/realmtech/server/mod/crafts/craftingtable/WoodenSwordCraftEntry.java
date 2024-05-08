package ch.realmtech.server.mod.crafts.craftingtable;

import ch.realmtech.server.craft.PatternArgs;
import ch.realmtech.server.registry.CraftPatternShape;

public class WoodenSwordCraftEntry extends CraftPatternShape {
    public WoodenSwordCraftEntry() {
        super("WoodenSwordCraft", "realmtech.items.WoodenSword", 1, new char[][]{
                {' ', 'b', ' '},
                {' ', 'b', ' '},
                {' ', 's', ' '},
        }, new PatternArgs('b', "realmtech.items.Plank"), new PatternArgs('s', "realmtech.items.Stick"));
    }
}
