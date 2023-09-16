package ch.realmtechServer;

import ch.realmtechCommuns.packet.Packet;
import ch.realmtechCommuns.packet.clientPacket.ConnectionJoueurReussitPacket;
import ch.realmtechCommuns.packet.serverPacket.DemandeDeConnectionJoueurPacket;
import ch.realmtechServer.ecs.EcsEngineServer;
import ch.realmtechServer.netty.ConnectionBuilder;
import ch.realmtechServer.netty.ConnectionCommand;
import ch.realmtechServer.netty.ServerNetty;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ServerContext {
    private final static Logger logger = LoggerFactory.getLogger(ServerContext.class);
    public final static Map<Integer, Function<ByteBuf, ? extends Packet>> packets = new HashMap<>();
    private final ServerNetty serverNetty;
    private final EcsEngineServer ecsEngineServer;

    static {
        Packet.addPacket(packets,
                ConnectionJoueurReussitPacket.class,
                DemandeDeConnectionJoueurPacket.class);
    }

    public ServerContext(ConnectionBuilder connectionBuilder) throws Exception {
        ecsEngineServer = new EcsEngineServer();
        ecsEngineServer.prepareSaveToLoad(connectionBuilder.getSaveName());
        serverNetty = new ServerNetty(connectionBuilder, ecsEngineServer.getWorld());
    }

    public static void main(String[] args) throws Exception {
        ConnectionCommand connectionCommand = new ConnectionCommand();
        new CommandLine(connectionCommand).parseArgs(args);
        new ServerContext(connectionCommand.call());
    }


    public ChannelFuture close() throws InterruptedException {
        return serverNetty.close();
    }


    public static ConnectionBuilder builder() {
        return new ConnectionBuilder();
    }
}
