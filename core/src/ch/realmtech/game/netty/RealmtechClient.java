package ch.realmtech.game.netty;

import ch.realmtechCommuns.packet.clientPacket.ClientExecute;
import ch.realmtechServer.netty.ConnexionBuilder;
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
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class RealmtechClient {
    private Channel channel;
    private NioEventLoopGroup group;
    private final ClientExecute clientExecute;

    /**
     * Initie directement une connexion
     */
    public RealmtechClient(ConnexionBuilder connexionBuilder, ClientExecute clientExecute) throws Exception {
        this.clientExecute = clientExecute;
        run(connexionBuilder);
    }

    private void run(ConnexionBuilder connexionBuilder) throws InterruptedException {
        this.group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                        ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                        ch.pipeline().addLast(new PacketDecoder());
                        ch.pipeline().addLast(new PacketEncoder());
                        ch.pipeline().addLast(new ClientHandler(clientExecute));
                    }
                });
        try {
            channel = b.connect(connexionBuilder.getHost(), connexionBuilder.getPort()).sync().channel();
        } catch (Exception e) {
            close();
            throw e;
        }
        channel.closeFuture().addListener(ChannelFutureListener.CLOSE);
    }

    public void close() {
        group.shutdownGracefully();
        if (channel != null) channel.close();
    }

    public Channel getChannel() {
        return channel;
    }
}
