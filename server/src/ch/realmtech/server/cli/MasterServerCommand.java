package ch.realmtech.server.cli;

import ch.realmtech.server.ServerContext;
import com.artemis.World;

import java.io.PrintWriter;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;

@Command(name = "server", aliases = "s", subcommands = {
        StopCommand.class,
        TeleportPlayerCommand.class,
        GiveCommand.class,
})
public class MasterServerCommand extends CommunMasterCommand implements Callable<Integer> {
    final ServerContext serverContext;

    public MasterServerCommand(ServerContext serverContext, PrintWriter output) {
        super(output);
        this.serverContext = serverContext;
    }

    @Override
    public Integer call() throws Exception {
        return 0;
    }

    @Override
    public World getWorld() {
        return serverContext.getEcsEngineServer().getWorld();
    }
}