package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class EnergyGeneratorItemEntry extends NewItemEntry {
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
