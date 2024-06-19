package ch.realmtech.server.mod.commandes;

import ch.realmtech.server.mod.commandes.masterCommand.MasterCommonCommandNew;
import ch.realmtech.server.registry.CommandEntry;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "echo", description = "Display a line of text.", mixinStandardHelpOptions = true)
public class NewEchoCommand extends CommandEntry {

    public NewEchoCommand() {
        super("Echo");
    }

    @ParentCommand
    private MasterCommonCommandNew masterCommonCommandNew;


    @Override
    public void run() {
        masterCommonCommandNew.output.println("echo new");
    }

    public void oui() {
        System.out.println("oui");
    }
}
