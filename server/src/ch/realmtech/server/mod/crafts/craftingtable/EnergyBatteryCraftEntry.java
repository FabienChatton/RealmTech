package ch.realmtech.server.mod.crafts.craftingtable;

import ch.realmtech.server.craft.PatternArgs;
import ch.realmtech.server.registry.CraftPatternShape;

public class EnergyBatteryCraftEntry extends CraftPatternShape {
    public EnergyBatteryCraftEntry() {
        super("EnergyBatterCraft", "realmtech.items.EnergyBattery", 1, new char[][]{
                {'p', 'c', 'p'},
                {'p', 'c', 'p'},
                {'p', 'c', 'p'},
        }, new PatternArgs('p', "realmtech.items.CopperIngot"), new PatternArgs('c', "realmtech.items.EnergyCable"));
    }
}
