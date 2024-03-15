package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class TinOreItemEntry extends NewItemEntry {
    public TinOreItemEntry() {
        super("TinOre", "tin-ore-01", ItemBehavior.builder()
                .build());
    }

    @Override
    public int getId() {
        return 290502642;
    }
}