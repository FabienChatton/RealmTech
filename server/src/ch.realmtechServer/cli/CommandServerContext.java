package ch.realmtechServer.cli;

import io.netty.channel.ChannelFuture;

import java.io.IOException;

public interface CommandServerContext extends CommendContext {
    ChannelFuture closeServer() throws IOException, InterruptedException;
}
