package ch.realmtech.server.serialize;

import ch.realmtech.server.serialize.chest.ChestSerializerManager;
import ch.realmtech.server.serialize.chest.ChestSerializerManagerController;
import ch.realmtech.server.serialize.chunk.ChunkSerializerManager;
import ch.realmtech.server.serialize.chunk.ChunkSerializerManagerController;
import ch.realmtech.server.serialize.inventory.InventorySerializerManager;
import ch.realmtech.server.serialize.inventory.InventorySerializerManagerController;

import java.util.Arrays;

public class SerializerController {
    private final static int MAGIC_NUMBER_LENGTH = 9;
    private final ChunkSerializerManager chunkSerializerManager = new ChunkSerializerManagerController();
    private final InventorySerializerManager inventorySerializerManager = new InventorySerializerManagerController();
    private final ChestSerializerManager chestSerializerManager = new ChestSerializerManagerController();

    SerializerManager<?, ?> getSerializerByMagicNumbers(byte[] bytes) {
        byte[] magicNumbers = new byte[9];
        System.arraycopy(bytes, 0, magicNumbers, 0, magicNumbers.length);
        if (Arrays.equals(magicNumbers, chunkSerializerManager.getMagicNumbers())) return chunkSerializerManager;
        if (Arrays.equals(magicNumbers, inventorySerializerManager.getMagicNumbers())) return inventorySerializerManager;
        if (Arrays.equals(magicNumbers, chestSerializerManager.getMagicNumbers())) return chestSerializerManager;

        return null;
    }

    public byte[] shrinkMagicNumbers(byte[] bytes) {
        byte[] shrinkBytes = new byte[bytes.length - MAGIC_NUMBER_LENGTH];
        System.arraycopy(bytes, MAGIC_NUMBER_LENGTH, shrinkBytes, 0, shrinkBytes.length);
        return shrinkBytes;
    }

    public ChunkSerializerManager getChunkSerializerManager() {
        return chunkSerializerManager;
    }

    public InventorySerializerManager getInventorySerializerManager() {
        return inventorySerializerManager;
    }

    public ChestSerializerManager getChestSerializerManager() {
        return chestSerializerManager;
    }
}
