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
import java.util.UUID;

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
    public void sendPacketToSubscriberForEntity(ClientPacket packet, UUID entitySubscription) {
        ImmutableIntBag<?> players = serverContext.getSystemsAdminServer().getPlayerSubscriptionSystem().getPlayerForEntityIdSubscription(entitySubscription);
        for (int i = 0; i < players.size(); i++) {
            int playerId = players.get(i);
            ComponentMapper<PlayerConnexionComponent> mPlayerConnexion = serverContext.getEcsEngineServer().getWorld().getMapper(PlayerConnexionComponent.class);
            PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);

            playerConnexionComponent.channel.write(packet);
        }
    }

    @Override
    public void sendPacketToSubscriberForChunkPos(ClientPacket packet, int chunkPosX, int chunkPosY) {
        ImmutableIntBag<?> players = serverContext.getSystemsAdminServer().getPlayerSubscriptionSystem().getPlayersInRangeForChunkPos(new Position(chunkPosX, chunkPosY));
        for (int i = 0; i < players.size(); i++) {
            int playerId = players.get(i);
            ComponentMapper<PlayerConnexionComponent> mPlayerConnexion = serverContext.getEcsEngineServer().getWorld().getMapper(PlayerConnexionComponent.class);
            PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);

            playerConnexionComponent.channel.write(packet);
        }
    }

    @Override
    public void sendPacketToSubscriberForChunkPosExcept(ClientPacket packet, int chunkPosX, int chunkPosY, Channel... notChannels) {
        ImmutableIntBag<?> players = serverContext.getSystemsAdminServer().getPlayerSubscriptionSystem().getPlayersInRangeForChunkPos(new Position(chunkPosX, chunkPosY));
        for (int i = 0; i < players.size(); i++) {
            int playerId = players.get(i);
            ComponentMapper<PlayerConnexionComponent> mPlayerConnexion = serverContext.getEcsEngineServer().getWorld().getMapper(PlayerConnexionComponent.class);
            PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);

            for (Channel notChannel : notChannels) {
                if (notChannel != playerConnexionComponent.channel) {
                    playerConnexionComponent.channel.write(packet);
                }
            }
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
