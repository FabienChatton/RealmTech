package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class EnergyBatteryCraftEntry extends NewCraftPatternShape {
    public EnergyBatteryCraftEntry() {
        super("energyBatterCraft", "realmtech.items.energyBattery", 1, new char[][]{
                {'p', 'c', 'p'},
                {'p', 'c', 'p'},
                {'p', 'c', 'p'},
        }, new NewPatternArgs('p', "realmtech.items.copperIngot"), new NewPatternArgs('c', "realmtech.items.energyCable"));
    }
}
