package ch.realmtechServer.netty;

import ch.realmtechCommuns.packet.Packet;
import ch.realmtechCommuns.packet.PlayerConnectionPacket;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import picocli.CommandLine;

import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RealmTechServer {
    public final static int PREFERRED_PORT = 25533;
    private Channel channel;
    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;
    public final static Map<Integer, Function<ByteBuf, ? extends Packet>> packets = new HashMap<>();

    public RealmTechServer(ConnectionBuilder connectionBuilder) throws Exception {
        Packet.addPacket(packets, PlayerConnectionPacket.class);
        run(connectionBuilder);
    }

    public static void main(String[] args) throws Exception {
        ConnectionCommand connectionCommand = new ConnectionCommand();
        new CommandLine(connectionCommand).parseArgs(args);
        new RealmTechServer(connectionCommand.call());
    }

    public static boolean isPortAvailable(int port) {
        boolean ret = false;
        try {
            (new ServerSocket(port)).close();
            ret = true;
        } catch (Exception ignored) {
        }
        return ret;
    }

    private void run(ConnectionBuilder connectionBuilder) throws Exception {
        this.boss = new NioEventLoopGroup();
        this.worker = new NioEventLoopGroup();

        ServerBootstrap sb = new ServerBootstrap();
        sb.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new PacketDecoder());
                        ch.pipeline().addLast(new PacketEncoder());
                        ch.pipeline().addLast(new ServerHandler());
                    }
                });
        channel = sb.bind(connectionBuilder.getPort()).sync().channel();
        System.out.println("Le serveur à overt sur le port " + connectionBuilder.getPort());
        channel.closeFuture().addListener(ChannelFutureListener.CLOSE);
    }


    public ChannelFuture close() throws InterruptedException {
        boss.shutdownGracefully();
        worker.shutdownGracefully();
        return channel.close();
    }


    public static ConnectionBuilder builder() {
        return new ConnectionBuilder();
    }
}
