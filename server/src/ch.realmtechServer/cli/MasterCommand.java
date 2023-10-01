package ch.realmtechServer.cli;

import ch.realmtechServer.ServerContext;

import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;

@Command(subcommands = {
        StopCommand.class,
})
public class MasterCommand implements Callable<Integer> {
    final ServerContext serverContext;

    public MasterCommand(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public Integer call() throws Exception {
        return 0;
    }
}
