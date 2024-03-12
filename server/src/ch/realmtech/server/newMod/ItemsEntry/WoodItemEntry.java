package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class WoodItemEntry extends NewItemEntry {
    public WoodItemEntry() {
        super("wood", "buche-01", ItemBehavior.builder()
                .setTimeToBurn(60)
                .build());
    }
}
