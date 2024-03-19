package ch.realmtech.server.mod.crafts.craftingtable;

import ch.realmtech.server.craft.PatternArgs;
import ch.realmtech.server.registry.CraftPatternShape;

public class EnergyCableCraftEntry extends CraftPatternShape {
    public EnergyCableCraftEntry() {
        super("EnergyCableCraft", "realmtech.items.EnergyCable", 1, new char[][]{
                {'c', 'c', 'c'}
        }, new PatternArgs('c', "realmtech.items.CopperIngot"));
    }
}
