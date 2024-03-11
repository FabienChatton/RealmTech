package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class EnergyCableItemEntry extends NewItemEntry {
    public EnergyCableItemEntry() {
        super("energyCable", "energy-cable-01-item", ItemBehavior.builder()
                .placeCell("realmtech.cells.energyCable")
                .build());
    }
}
