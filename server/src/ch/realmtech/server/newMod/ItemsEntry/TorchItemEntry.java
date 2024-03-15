package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class TorchItemEntry extends NewItemEntry {
    public TorchItemEntry() {
        super("Torch", "torch-01", ItemBehavior.builder()
                .placeCell("realmtech.cells.Torch")
                .build());
    }

    @Override
    public int getId() {
        return 1395027571;
    }
}
