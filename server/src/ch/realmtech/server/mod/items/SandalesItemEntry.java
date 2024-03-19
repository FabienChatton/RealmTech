package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class SandalesItemEntry extends ItemEntry {
    public SandalesItemEntry() {
        super("Sandales", "sandales-01", ItemBehavior.builder()
                .setSpeedEffect(2)
                .build());
    }

    @Override
    public int getId() {
        return -233843002;
    }
}
