package ch.realmtech.server.serialize;


import ch.realmtech.server.serialize.inventory.InventorySerializerCoder;

public interface SerializerControllerItf<InputType, OutputType> extends InventorySerializerCoder, SerializerManager<InputType, OutputType> {
}
