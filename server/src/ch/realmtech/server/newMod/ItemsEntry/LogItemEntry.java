package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class LogItemEntry extends NewItemEntry {
    public LogItemEntry() {
        super("log", "buche-01", ItemBehavior.builder()
                .build());
    }
}
