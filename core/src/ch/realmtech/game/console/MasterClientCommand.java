package ch.realmtech.game.console;


import ch.realmtech.RealmTech;

import java.io.PrintWriter;

import static picocli.CommandLine.Command;

@Command(name = "client", aliases = "c", subcommands = {
    EchoClientCommand.class,
})
public class MasterClientCommand {
    final RealmTech context;
    final PrintWriter output;
    final String stringCommand;

    public MasterClientCommand(RealmTech context, PrintWriter output, String stringCommand) {
        this.context = context;
        this.output = output;
        this.stringCommand = stringCommand;
    }
}
