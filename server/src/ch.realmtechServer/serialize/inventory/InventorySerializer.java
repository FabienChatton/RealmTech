package ch.realmtechServer.serialize.inventory;

import ch.realmtechServer.ecs.component.InventoryComponent;
import com.artemis.World;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

public interface InventorySerializer {
    /** serializer un inventory en un tableau de byte */
    byte[] toBytes(InventoryComponent inventoryComponent, World world);
    /**
     * Permet de donner un supplier de Inventory entity à partir d'un tableau de byte qui represent l'inventory component.
     * La méthode ne créer pas de nouvel entity dans le monde, c'est le supplier qui créer l'entity et le return
     * l'id de l'entity. Quand le supplier est invoqué, c'est l'inventaire qui est créé, mais aussi tous les items.
     */
    Supplier<Integer> fromBytes(World world, byte[] bytes);

    InventorySerializerV1 inventorySerializerV1 = new InventorySerializerV1();

    static Supplier<Integer> getFromBytes(World world, byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES);
        byteBuffer.put(bytes);
        int version = byteBuffer.getInt();

        return switch (version) {
            case 1 -> inventorySerializerV1.fromBytes(world, bytes);
            default -> throw new IllegalStateException("Unexpected value: " + version + ". Cette version n'est pas implémentée pas");
        };
    }
}
