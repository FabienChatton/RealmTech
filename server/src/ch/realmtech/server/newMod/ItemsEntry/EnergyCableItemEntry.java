package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class EnergyCableItemEntry extends NewItemEntry {
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
