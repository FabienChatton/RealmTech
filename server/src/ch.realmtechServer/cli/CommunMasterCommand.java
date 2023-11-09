package ch.realmtechServer.cli;

import java.io.PrintWriter;

import static picocli.CommandLine.Command;

@Command(name = "commun", aliases = "c", subcommands = {
        EchoCommande.class,
        DumpCommand.class,
        RuntimeInfoCommand.class,
})
public abstract class CommunMasterCommand implements CommendContext {
    final PrintWriter output;

    public CommunMasterCommand(PrintWriter output) {
        this.output = output;
    }
}
