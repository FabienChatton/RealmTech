package ch.realmtech.server.netty;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.divers.Position;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.packet.ClientPacket;
import ch.realmtech.server.packet.ServerConnexion;
import ch.realmtech.server.packet.clientPacket.ClientExecute;
import com.artemis.ComponentMapper;
import com.artemis.utils.ImmutableIntBag;
import io.netty.channel.Channel;

import java.util.UUID;

public class ServerConnexionIntern implements ServerConnexion {
    private final ClientExecute clientExecute;
    private final ServerContext serverContext;

    public ServerConnexionIntern(ServerContext serverContext, ClientExecute clientExecute) {
        this.serverContext = serverContext;
        this.clientExecute = clientExecute;
    }

    @Override
    public void broadCastPacket(ClientPacket packet) {
        packet.executeOnClient(clientExecute);
    }

    @Override
    public void sendPacketToSubscriberForEntity(ClientPacket packet, UUID entitySubscription) {
        ImmutableIntBag<?> players = serverContext.getSystemsAdminServer().getPlayerSubscriptionSystem().getPlayerForEntityIdSubscription(entitySubscription);
        for (int i = 0; i < players.size(); i++) {
            packet.executeOnClient(clientExecute);
        }
    }

    @Override
    public void sendPacketToSubscriberForChunkPos(ClientPacket packet, int chunkPosX, int chunkPosY) {
        ImmutableIntBag<?> players = serverContext.getSystemsAdminServer().getPlayerSubscriptionSystem().getPlayersInRangeForChunkPos(new Position(chunkPosX, chunkPosY));
        for (int i = 0; i < players.size(); i++) {
            packet.executeOnClient(clientExecute);
        }
    }

    @Override
    public void sendPacketToSubscriberForChunkPosExcept(ClientPacket packet, int chunkPosX, int chunkPosY, Channel... notChannels) {
        ImmutableIntBag<?> players = serverContext.getSystemsAdminServer().getPlayerSubscriptionSystem().getPlayersInRangeForChunkPos(new Position(chunkPosX, chunkPosY));
        for (int i = 0; i < players.size(); i++) {
            int playerId = players.get(i);
            ComponentMapper<PlayerConnexionComponent> mPlayerConnexion = serverContext.getEcsEngineServer().getWorld().getMapper(PlayerConnexionComponent.class);
            PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);

            for (Channel channel : notChannels) {
                if (channel != playerConnexionComponent.channel) {
                    packet.executeOnClient(clientExecute);
                }
            }
        }
    }

    @Override
    public void broadCastPacketExcept(ClientPacket packet, Channel... channels) {
        if (channels.length != 1) {
            packet.executeOnClient(clientExecute);
        }
    }

    @Override
    public void sendPacketTo(ClientPacket packet, Channel channel) {
        packet.executeOnClient(clientExecute);
    }
}
