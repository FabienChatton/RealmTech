package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class CoalOreItemEntry extends NewItemEntry {
    public CoalOreItemEntry() {
        super("CoalOre", "coal-ore-01", ItemBehavior.builder()
                .build());
    }

    @Override
    public int getId() {
        return -1626413438;
    }
}
