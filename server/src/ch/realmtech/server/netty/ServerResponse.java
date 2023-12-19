package ch.realmtech.server.netty;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.packet.ClientPacket;
import ch.realmtech.server.packet.ServerResponseHandler;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerResponse implements ServerResponseHandler {
    private final static Logger logger = LoggerFactory.getLogger(ServerResponse.class);
    private final ServerContext serverContext;

    public ServerResponse(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public void broadCastPacket(ClientPacket packet) {
        logger.trace("Send packet {} in broadCast", packet.getClass().getSimpleName());
        ServerHandler.channels.forEach(channel -> channel.write(packet));
    }

    @Override
    public void broadCastPacketInRange(ClientPacket packet, int playersSrc) {
        IntBag players = serverContext.getSystemsAdmin().playerManagerServer.getPlayers();
        ComponentMapper<PositionComponent> mPos = serverContext.getEcsEngineServer().getWorld().getMapper(PositionComponent.class);
        ComponentMapper<PlayerConnexionComponent> mPlayerConnexion = serverContext.getEcsEngineServer().getWorld().getMapper(PlayerConnexionComponent.class);

        PositionComponent srcPositionComponent = mPos.get(playersSrc);
        int srcChunkPosX = MapManager.getChunkPos(MapManager.getWorldPos(srcPositionComponent.x));
        int srcChunkPosY = MapManager.getChunkPos(MapManager.getWorldPos(srcPositionComponent.y));

        List<Channel> channelInRange = new ArrayList<>(players.size() - 1);
        int renderDistance = serverContext.getOptionServer().renderDistance.get();
        int[] playersData = players.getData();
        for (int i = 0; i < players.size(); i++) {
            int playerId = playersData[i];
            if (playerId == playersSrc) continue; // don't send to src player

            PositionComponent dstPositionComponent = mPos.get(playerId);
            int dstChunkPosX = MapManager.getChunkPos(MapManager.getWorldPos(dstPositionComponent.x));
            int dstChunkPosY = MapManager.getChunkPos(MapManager.getWorldPos(dstPositionComponent.y));

            if (Math.abs(dstChunkPosX - srcChunkPosX) <= renderDistance &&
                Math.abs(dstChunkPosY - srcChunkPosY) <= renderDistance) {
                channelInRange.add(mPlayerConnexion.get(playerId).channel);
            }
        }

        for (Channel channel : channelInRange) {
            sendPacketTo(packet, channel);
        }
    }

    @Override
    public void broadCastPacketExcept(ClientPacket packet, Channel... channels) {
        if (channels.length == 0) {
            logger.warn("Warn, a packet was send to nobody");
        }
        List<Channel> channelFiltre = ServerHandler.channels.stream()
                .filter(channel -> Arrays.stream(channels).anyMatch(c -> c != channel))
                .toList();
        logger.trace("Send packet {} to {}", packet.getClass().getSimpleName(), channelFiltre);
        channelFiltre
                .forEach(channel -> channel.write(packet));
    }

    @Override
    public void sendPacketTo(ClientPacket packet, Channel... channels) {
        if (channels.length == 0) {
            logger.warn("Warn, a packet was send to nobody");
        }
        logger.trace("Send packet {} to {}", packet.getClass().getSimpleName(), Arrays.toString(channels));
        Arrays.stream(channels)
                .forEach(channel -> channel.write(packet));
    }

    @Override
    public void sendPacketTo(ClientPacket packet, Channel channel) {
        logger.trace("Send packet {} to {}", packet.getClass().getSimpleName(), channel);
        channel.write(packet);
    }
}
