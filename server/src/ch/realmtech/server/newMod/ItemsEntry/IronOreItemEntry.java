package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class IronOreItemEntry extends NewItemEntry {
    public IronOreItemEntry() {
        super("ironOre", "iron-ore-01", ItemBehavior.builder()
                .build());
    }
}
