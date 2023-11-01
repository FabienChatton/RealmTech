package ch.realmtechServer.cli;

import java.io.PrintWriter;

import static picocli.CommandLine.*;

@Command(name = "commun", aliases = "c", subcommands = {
        EchoCommande.class,
        DumpItemsCommand.class,
})
public abstract class CommunMasterCommand implements CommendContext {
    final PrintWriter output;

    public CommunMasterCommand(PrintWriter output) {
        this.output = output;
    }
}
