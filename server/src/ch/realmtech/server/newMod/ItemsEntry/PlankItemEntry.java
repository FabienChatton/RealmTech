package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class PlankItemEntry extends NewItemEntry {
    public PlankItemEntry() {
        super("plank", "plank-02", ItemBehavior.builder()
                .setTimeToBurn(60)
                .build());
    }
}
