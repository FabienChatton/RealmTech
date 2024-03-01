package ch.realmtech.server.netty;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.divers.Position;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.packet.ClientPacket;
import ch.realmtech.server.packet.ServerConnexion;
import com.artemis.ComponentMapper;
import com.artemis.utils.ImmutableIntBag;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class ServerConnexionExtern implements ServerConnexion {
    private final static Logger logger = LoggerFactory.getLogger(ServerConnexionExtern.class);
    private final ServerContext serverContext;

    public ServerConnexionExtern(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public void broadCastPacket(ClientPacket packet) {
        logger.trace("Send packet {} in broadCast", packet.getClass().getSimpleName());
        ServerHandler.channels.forEach(channel -> channel.write(packet));
    }

    @Override
    public void sendPacketToSubscriberForEntityId(ClientPacket packet, int entityIdSubscription) {
        ImmutableIntBag<?> players = serverContext.getSystemsAdmin().playerSubscriptionSystem.getPlayerForEntityIdSubscription(entityIdSubscription);
        for (int i = 0; i < players.size(); i++) {
            int playerId = players.get(i);
            ComponentMapper<PlayerConnexionComponent> mPlayerConnexion = serverContext.getEcsEngineServer().getWorld().getMapper(PlayerConnexionComponent.class);
            PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);

            playerConnexionComponent.channel.write(packet);
        }
    }

    @Override
    public void sendPacketToSubscriberForChunkPos(ClientPacket packet, int chunkPosX, int chunkPosY) {
        ImmutableIntBag<?> players = serverContext.getSystemsAdmin().playerSubscriptionSystem.getPlayersInRangeForChunkPos(new Position(chunkPosX, chunkPosY));
        for (int i = 0; i < players.size(); i++) {
            int playerId = players.get(i);
            ComponentMapper<PlayerConnexionComponent> mPlayerConnexion = serverContext.getEcsEngineServer().getWorld().getMapper(PlayerConnexionComponent.class);
            PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);

            playerConnexionComponent.channel.write(packet);
        }
    }

    @Override
    public void broadCastPacketExcept(ClientPacket packet, Channel... channels) {
        if (channels.length == 0) {
            logger.warn("Warn, a packet was send to nobody: {}", packet.getClass().getSimpleName());
        }
        List<Channel> channelFiltre = ServerHandler.channels.stream()
                .filter(channel -> Arrays.stream(channels).anyMatch(c -> c != channel))
                .toList();
        logger.trace("Send packet {} to {}", packet.getClass().getSimpleName(), channelFiltre);
        channelFiltre
                .forEach(channel -> channel.write(packet));
    }

    @Override
    public void sendPacketTo(ClientPacket packet, Channel channel) {
        logger.trace("Send packet {} to {}", packet.getClass().getSimpleName(), channel);
        channel.write(packet);
    }
}
