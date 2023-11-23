package ch.realmtech.server.serialize;

class SerializerControllerTest {
//    private final SerializerController serializerController = new SerializerController();
//
//    private byte[] concatArray(byte[] a, byte[] b) {
//        byte[] ret = new byte[a.length + b.length];
//        int i = 0;
//        for (byte magicNumber : a) {
//            ret[i++] = magicNumber;
//        }
//        for (byte datum : b) {
//            ret[i++] = datum;
//        }
//        return ret;
//    }
//
//    @Test
//    void shrinkMagicNumbersTest() {
//        byte[] magicNumbersChunk = serializerController.getChunkSerializerManager().getMagicNumbers();
//        byte[] data = new byte[]{32, 23, 13, -23, -4, 2};
//
//        byte[] arrayToShrink = concatArray(magicNumbersChunk, data);
//
//        assertArrayEquals(data, serializerController.shrinkMagicNumbers(arrayToShrink));
//    }
//
//    @Test
//    void getSerializerByMagicNumbersChunkTest() {
//        ChunkSerializerManager chunkSerializerManager = serializerController.getChunkSerializerManager();
//        byte[] magicNumbers = chunkSerializerManager.getMagicNumbers();
//        byte[] data = new byte[]{32, 23, 13, -23, -4, 2};
//
//        byte[] dataWithMagic = concatArray(magicNumbers, data);
//        SerializerManager<?, ?> serializer = serializerController.getSerializerByMagicNumbers(dataWithMagic);
//        assertSame(chunkSerializerManager, serializer);
//    }
//
//    @Test
//    void getSerializerByMagicNumbersInventoryTest() {
//        InventorySerializerManager inventorySerializerManager = serializerController.getInventorySerializerManager();
//        byte[] magicNumbers = inventorySerializerManager.getMagicNumbers();
//        byte[] data = new byte[]{32, 23, 13, -23, -4, 2};
//
//        byte[] dataWithMagic = concatArray(magicNumbers, data);
//        SerializerManager<?, ?> serializer = serializerController.getSerializerByMagicNumbers(dataWithMagic);
//        assertSame(inventorySerializerManager, serializer);
//    }
//
//    @Test
//    void getSerializerByMagicNumbersChestTest() {
//        ChestSerializerManager chestSerializerManager = serializerController.getChestSerializerManager();
//        byte[] magicNumbers = chestSerializerManager.getMagicNumbers();
//        byte[] data = new byte[]{32, 23, 13, -23, -4, 2};
//
//        byte[] dataWithMagic = concatArray(magicNumbers, data);
//        SerializerManager<?, ?> serializer = serializerController.getSerializerByMagicNumbers(dataWithMagic);
//        assertSame(chestSerializerManager, serializer);
//    }
}