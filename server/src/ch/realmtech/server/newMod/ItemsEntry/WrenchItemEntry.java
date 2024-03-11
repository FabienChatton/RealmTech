package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.level.WrenchRightClick;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class WrenchItemEntry extends NewItemEntry {
    public WrenchItemEntry() {
        super("wrench", "wrench-01", ItemBehavior.builder()
                .interagieClickDroit(WrenchRightClick.wrenchRightClick())
                .build());
    }
}
