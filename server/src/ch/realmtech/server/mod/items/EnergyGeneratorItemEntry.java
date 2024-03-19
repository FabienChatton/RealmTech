package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class EnergyGeneratorItemEntry extends ItemEntry {
    public EnergyGeneratorItemEntry() {
        super("EnergyGenerator", "furnace-01", ItemBehavior.builder()
                .placeCell("realmtech.cells.EnergyGenerator")
                .build());
    }

    @Override
    public int getId() {
        return -560459038;
    }
}
