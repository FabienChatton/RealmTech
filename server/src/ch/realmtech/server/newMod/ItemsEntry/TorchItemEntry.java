package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class TorchItemEntry extends NewItemEntry {
    public TorchItemEntry() {
        super("torch", "torch-01", ItemBehavior.builder()
                .placeCell("realmtech.cells.torch")
                .build());
    }

    @Override
    public int getId() {
        return 1395027571;
    }
}
