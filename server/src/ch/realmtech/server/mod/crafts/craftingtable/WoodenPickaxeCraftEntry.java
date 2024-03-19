package ch.realmtech.server.mod.crafts.craftingtable;

import ch.realmtech.server.craft.PatternArgs;
import ch.realmtech.server.registry.CraftPatternShape;

public class WoodenPickaxeCraftEntry extends CraftPatternShape {
    public WoodenPickaxeCraftEntry() {
        super("WoodenPickaxeCraft", "realmtech.items.WoodenPickaxe", 1, new char[][]{
                {'p', 'p', 'p'},
                {' ', 's', ' '},
                {' ', 's', ' '}
        }, new PatternArgs('p', "realmtech.items.Plank"), new PatternArgs('s', "realmtech.items.Stick"));
    }
}
