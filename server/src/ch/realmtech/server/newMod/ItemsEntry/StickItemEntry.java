package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class StickItemEntry extends NewItemEntry {
    public StickItemEntry() {
        super("Stick", "stick-02", ItemBehavior.builder()
                .build());
    }

    @Override
    public int getId() {
        return 1394244359;
    }
}
