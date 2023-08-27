package ch.realmtech.game.netty;

import ch.realmtechServer.netty.RealmTechServer;
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

    public RealmtechClient() throws Exception {
        this("localhost");
    }

    public RealmtechClient(String host) throws Exception {
        run(host);
    }

    private void run(String host) throws InterruptedException {
        this.group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });
        channel = b.connect(host, RealmTechServer.PORT).sync().channel();
        channel.closeFuture().addListener(ChannelFutureListener.CLOSE);
    }

    public ChannelFuture close() {
        group.shutdownGracefully();
        return channel.close();
    }
}
