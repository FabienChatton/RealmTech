package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class StoneOreItemEntry extends NewItemEntry {
    public StoneOreItemEntry() {
        super("stoneOre", "stone-ore-01", ItemBehavior.builder()
                .build());
    }

    @Override
    public int getId() {
        return 1394250460;
    }
}
