package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class GoldOreItemEntry extends NewItemEntry {
    public GoldOreItemEntry() {
        super("goldOre", "gold-ore-01", ItemBehavior.builder()
                .build());
    }

    @Override
    public int getId() {
        return 1933521689;
    }
}
