package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class EnergyBatteryCraftEntry extends NewCraftPatternShape {
    public EnergyBatteryCraftEntry() {
        super("EnergyBatterCraft", "realmtech.items.EnergyBattery", 1, new char[][]{
                {'p', 'c', 'p'},
                {'p', 'c', 'p'},
                {'p', 'c', 'p'},
        }, new NewPatternArgs('p', "realmtech.items.CopperIngot"), new NewPatternArgs('c', "realmtech.items.EnergyCable"));
    }
}
