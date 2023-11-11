package ch.realmtech.server.serialize.inventory;

import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.ecs.component.InventoryComponent;
import com.artemis.World;

import java.nio.ByteBuffer;
import java.util.function.Function;

public interface InventorySerializer {
    /** serializer un inventory en un tableau de byte */
    byte[] toBytes(InventoryComponent inventoryComponent, World world);
    /**
     * Permet de donner un supplier de Inventory entity à partir d'un tableau de byte qui represent l'inventory component.
     * La méthode ne créer pas de nouvel entity dans le monde, c'est le supplier qui créer les entities et le return
     * le tableau de l'inventaire. Quand le supplier est invoqué, c'est l'inventaire qui est créé, mais aussi tous les items.
     */
    Function<ItemManager, int[][]> fromBytes(World world, byte[] bytes);

    static Function<ItemManager, int[][]> getFromBytes(World world, byte[] bytes) {
        byte[] versionBytes = new byte[Integer.BYTES];
        System.arraycopy(bytes, 0, versionBytes, 0, versionBytes.length);
        ByteBuffer byteBuffer = ByteBuffer.wrap(versionBytes);
        int version = byteBuffer.getInt();

        return switch (version) {
            case 1 -> InventorySerializerImplementation.inventorySerializerV1.fromBytes(world, bytes);
            default -> throw new IllegalStateException("Unexpected value: " + version + ". Cette version n'est pas implémentée");
        };
    }

    static byte[] toBytes(World world, InventoryComponent inventoryComponent) {
        return InventorySerializerImplementation.inventorySerializerV1.toBytes(inventoryComponent, world);
    }

    final class InventorySerializerImplementation {
        private static final InventorySerializer inventorySerializerV1 = new InventorySerializerV1();
    }
}
