package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class EnergyCableItemEntry extends ItemEntry {
    public EnergyCableItemEntry() {
        super("EnergyCable", "energy-cable-01-item", ItemBehavior.builder()
                .placeCell("realmtech.cells.EnergyCable")
                .build());
    }

    @Override
    public int getId() {
        return 235045836;
    }
}
