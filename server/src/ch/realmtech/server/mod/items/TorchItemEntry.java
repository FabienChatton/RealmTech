package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class TorchItemEntry extends ItemEntry {
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
