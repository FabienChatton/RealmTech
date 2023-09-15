package ch.realmtechCommuns.packet;

import com.artemis.BaseSystem;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Function;


public interface Packet {
    Logger logger = LoggerFactory.getLogger(Packet.class);
    void write(ByteBuf byteBuf);

    default int getId() {
        return getClass().getSimpleName().hashCode();
    }

    static int getId(Class<? extends Packet> packet) {
        int packetId = packet.getSimpleName().hashCode();
        logger.trace("calcule du packet id {} du packet {}", packetId, packet);
        return packetId;
    }

    static void addPacket(Map<Integer, Function<ByteBuf, ? extends Packet>> packetsMap, Class<? extends Packet> ...packets) {
        for (Class<? extends Packet> packet : packets) {
            int id = getId(packet);
            packetsMap.put(id, byteBuf -> {
                try {
                    logger.trace("ajoute du packet {} dans la liste des packet", packet);
                    return packet.getConstructor(ByteBuf.class).newInstance(byteBuf);
                } catch (InstantiationException e) {
                    logger.error("Impossible d'instancier un packet ", e);
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    logger.error("Le constructeur n'a pas été trouvé", e);
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    logger.error("Une erreur d'invocation c'est produite", e);
                    throw new RuntimeException(e);
                } catch(IllegalAccessException e) {
                    logger.error("Le constructeur n'est pas publique", e);
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
