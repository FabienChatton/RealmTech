package ch.realmtechServer.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class RealmTechServer {
    public final static int PORT = 25533;
    private Channel channel;
    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;

    public RealmTechServer() throws Exception {
        run();
    }

    public static void main(String[] args) throws Exception {
        new RealmTechServer();
    }

    private void run() throws Exception {
        this.boss = new NioEventLoopGroup();
        this.worker = new NioEventLoopGroup();

        ServerBootstrap sb = new ServerBootstrap();
        sb.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ServerHandler());
                    }
                });
        channel = sb.bind(PORT).sync().channel();
        channel.closeFuture().addListener(ChannelFutureListener.CLOSE);
    }


    public ChannelFuture close() throws InterruptedException {
        boss.shutdownGracefully();
        worker.shutdownGracefully();
        return channel.close();
    }
}
