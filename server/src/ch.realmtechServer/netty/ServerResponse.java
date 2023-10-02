package ch.realmtechServer.netty;

import ch.realmtechServer.packet.ClientPacket;
import ch.realmtechServer.packet.ServerResponseHandler;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class ServerResponse implements ServerResponseHandler {
    private final static Logger logger = LoggerFactory.getLogger(ServerResponse.class);
    @Override
    public void broadCastPacket(ClientPacket packet) {
        logger.trace("envoie du packet {} en broadCast", packet.getClass().getSimpleName());
        ServerHandler.channels.forEach(channel -> channel.writeAndFlush(packet));
    }

    @Override
    public void broadCastPacketExcept(ClientPacket packet, Channel... channels) {
        if (channels.length == 0) {
            logger.warn("Attention, un packet a été envoyé vers personne");
        }
        List<Channel> channelFiltre = ServerHandler.channels.stream()
                .filter(channel -> Arrays.stream(channels).anyMatch(c -> c != channel))
                .toList();
        logger.trace("envoie du packet {} vers {}", packet.getClass().getSimpleName(), channelFiltre);
        channelFiltre
                .forEach(channel -> channel.writeAndFlush(packet));
    }

    @Override
    public void sendPacketTo(ClientPacket packet, Channel... channels) {
        if (channels.length == 0) {
            logger.warn("Attention, un packet a été envoyé vers personne");
        }
        logger.trace("envoie du packet {} vers {}", packet.getClass().getSimpleName(), Arrays.toString(channels));
        Arrays.stream(channels)
                .forEach(channel -> channel.writeAndFlush(packet));
    }
}
