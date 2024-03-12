package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class CoalItemEntry extends NewItemEntry {
    public CoalItemEntry() {
        super("coal", "coal-01", ItemBehavior.builder()
                .setTimeToBurn(1000)
                .build());
    }

    @Override
    public int getId() {
        return -1063884736;
    }
}
