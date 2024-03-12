package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class SandalesItemEntry extends NewItemEntry {
    public SandalesItemEntry() {
        super("sandales", "sandales-01", ItemBehavior.builder()
                .setSpeedEffect(2)
                .build());
    }

    @Override
    public int getId() {
        return -233843002;
    }
}
