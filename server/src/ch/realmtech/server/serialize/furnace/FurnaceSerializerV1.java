package ch.realmtech.server.serialize.furnace;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.FurnaceComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.function.Consumer;

public class FurnaceSerializerV1 implements Serializer<Integer, Consumer<Integer>> {
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer furnaceToSerialize) {
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, furnaceToSerialize));

        FurnaceComponent furnaceComponent = world.getMapper(FurnaceComponent.class).get(furnaceToSerialize);
        ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);

        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getCraftingTableController(), furnaceComponent.craftingTableId);
        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager(), mInventory.get(furnaceComponent.inventoryCarburant));
        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public Consumer<Integer> fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());

        return (motherId) -> {};
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer furnaceToSerialize) {
        return 0;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
