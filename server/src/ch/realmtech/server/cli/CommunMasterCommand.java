package ch.realmtech.server.cli;

import java.io.PrintWriter;

import static picocli.CommandLine.Command;

@Command(name = "commun", aliases = "c", subcommands = {
        EchoCommande.class,
        DumpCommand.class,
        RuntimeInfoCommand.class,
        OptionsCommand.class,
        WhereCommand.class,
})
public abstract class CommunMasterCommand implements CommendContext {
    public final PrintWriter output;

    public CommunMasterCommand(PrintWriter output) {
        this.output = output;
    }
}
