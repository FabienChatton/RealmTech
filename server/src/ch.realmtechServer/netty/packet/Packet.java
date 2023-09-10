package ch.realmtechServer.netty.packet;

import io.netty.buffer.ByteBuf;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Function;


public interface Packet {
    void write(ByteBuf byteBuf);

    default int getId() {
        return getClass().getSimpleName().hashCode();
    }

    static int getId(Class<? extends Packet> packet) {
        return packet.getSimpleName().hashCode();
    }

    static void addPacket(Map<Integer, Function<ByteBuf, ? extends Packet>> packets, Class<? extends Packet> packet) {
        int id = getId(packet);
        packets.put(id, byteBuf -> {
            try {
                return packet.getConstructor(ByteBuf.class).newInstance(byteBuf);
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
