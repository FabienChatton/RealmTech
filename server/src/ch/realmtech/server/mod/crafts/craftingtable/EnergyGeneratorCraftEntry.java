package ch.realmtech.server.mod.crafts.craftingtable;

import ch.realmtech.server.registry.CraftPatternShapeless;

public class EnergyGeneratorCraftEntry extends CraftPatternShapeless {
    public EnergyGeneratorCraftEntry() {
        super("EnergyGeneratorCraft", "realmtech.items.EnergyGenerator",
                "realmtech.items.Furnace",
                "realmtech.items.EnergyBattery"
        );
    }
}
