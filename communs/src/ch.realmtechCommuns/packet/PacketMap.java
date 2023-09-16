package ch.realmtechCommuns.packet;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.function.Function;

public class PacketMap extends HashMap<Integer, Function<ByteBuf, ? extends Packet>> {

    public PacketMap put(Class<?> packetClazz, Function<ByteBuf, ? extends Packet> packet) {
        put(packetClazz.getSimpleName().hashCode(), packet);
        return this;
    }
}
