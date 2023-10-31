package ch.realmtechServer.cli;

import ch.realmtechServer.ServerContext;
import com.artemis.World;
import io.netty.channel.ChannelFuture;

import java.io.IOException;

public class CommandeServerServerContext implements CommandServerContext {
    private final ServerContext serverContext;

    public CommandeServerServerContext(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public World getWorld() {
        return serverContext.getEcsEngineServer().getWorld();
    }

    @Override
    public ChannelFuture closeServer() throws IOException, InterruptedException {
        return serverContext.close();
    }
}
