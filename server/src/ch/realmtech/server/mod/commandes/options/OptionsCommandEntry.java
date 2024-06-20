package ch.realmtech.server.mod.commandes.options;

import ch.realmtech.server.mod.commandes.masterCommand.MasterServerCommandNew;
import ch.realmtech.server.registry.CommandEntry;
import picocli.CommandLine;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "options", aliases = "option", description = "master commande of option for server. see sub commande for more info")
public class OptionsCommandEntry extends CommandEntry {
    public OptionsCommandEntry() {
        super("Options");
    }

    @ParentCommand
    public MasterServerCommandNew masterCommand;

    @Override
    public void run() {
        CommandLine commandLine = new CommandLine(this);
        commandLine.setErr(masterCommand.output);
        commandLine.setOut(masterCommand.output);
        commandLine.usage(masterCommand.output);
    }
}
