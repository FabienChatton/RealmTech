package ch.realmtechServer.packet;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;

public class PacketMap extends HashMap<Integer, Function<ByteBuf, ? extends Packet>> {
    private final static Logger logger = LoggerFactory.getLogger(PacketMap.class);

    public PacketMap put(Class<?> packetClazz, Function<ByteBuf, ? extends Packet> packet) {
        put(packetClazz.getSimpleName().hashCode(), packet);
        return this;
    }

    @Override
    public Function<ByteBuf, ? extends Packet> get(Object key) {
        try {
            return super.get(key);
        } catch (NullPointerException e) {
            logger.error("Un erreur c'est produite, il manque peut être un packet");
            logger.error("id: {}", key);
            logger.error("tous les id {}", Arrays.toString(super.keySet().toArray()));
            logger.info("Tous les packets {}", Arrays.toString(super.values().toArray()));
            throw e;
        }
    }
}
