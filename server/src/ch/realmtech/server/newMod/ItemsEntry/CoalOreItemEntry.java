package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class CoalOreItemEntry extends NewItemEntry {
    public CoalOreItemEntry() {
        super("coalOre", "coal-ore-01", ItemBehavior.builder()
                .build());
    }
}
