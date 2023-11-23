package ch.realmtech.server.serialize;

import ch.realmtech.server.serialize.inventory.InventorySerializerCoder;


public class SerializerController {
    private final InventorySerializerController inventorySerializerManager = new InventorySerializerController(this);

    public InventorySerializerCoder getInventorySerializerManager() {
        return inventorySerializerManager;
    }
}
