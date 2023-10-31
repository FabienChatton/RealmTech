package ch.realmtechServer.cli;

import java.io.PrintWriter;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;

@Command(name = "server", aliases = "s", subcommands = {
        StopCommand.class,
        EchoCommande.class,
        DumpItemsCommand.class,
})
public class MasterCommand implements Callable<Integer> {
    final CommandServerContext commandServerContext;
    final PrintWriter output;

    public MasterCommand(CommandServerContext commandServerContext, PrintWriter output) {
        this.commandServerContext = commandServerContext;
        this.output = output;
    }

    @Override
    public Integer call() throws Exception {
        return 0;
    }
}
