package ch.realmtech.game.netty;

import ch.realmtech.RealmTech;
import ch.realmtechServer.netty.ConnectionBuilder;
import ch.realmtechServer.netty.PacketDecoder;
import ch.realmtechServer.netty.PacketEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RealmtechClient {
    private Channel channel;
    private NioEventLoopGroup group;
    private final RealmTech context;

    /**
     * Initie directement une connection
     */
    public RealmtechClient(ConnectionBuilder connectionBuilder, RealmTech context) throws Exception {
        this.context = context;
        run(connectionBuilder);
    }

    private void run(ConnectionBuilder connectionBuilder) throws InterruptedException {
        this.group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new PacketDecoder());
                        ch.pipeline().addLast(new PacketEncoder());
                        ch.pipeline().addLast(new ClientHandler(context.getEcsEngine().getWorld()));
                    }
                });
        channel = b.connect(connectionBuilder.getHost(), connectionBuilder.getPort()).sync().channel();
        channel.closeFuture().addListener(ChannelFutureListener.CLOSE);
    }

    public ChannelFuture close() {
        group.shutdownGracefully();
        return channel.close();
    }

    public Channel getChannel() {
        return channel;
    }
}
