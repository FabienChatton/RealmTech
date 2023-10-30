package ch.realmtechServer.cli;

import ch.realmtechServer.ServerContext;

import java.io.PrintWriter;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;

@Command(name = "rtc", aliases = "", subcommands = {
        StopCommand.class,
        EchoCommande.class,
})
public class MasterCommand implements Callable<Integer> {
    final ServerContext serverContext;
    final PrintWriter output;

    public MasterCommand(ServerContext serverContext, PrintWriter output) {
        this.serverContext = serverContext;
        this.output = output;
    }

    @Override
    public Integer call() throws Exception {
        return 0;
    }
}
