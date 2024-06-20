package ch.realmtech.server.mod.commandes;

import ch.realmtech.server.mod.commandes.masterCommand.MasterCommonCommandNew;
import ch.realmtech.server.registry.CommandEntry;

import static picocli.CommandLine.*;

@Command(name = "echo", description = "Display a line of text.", mixinStandardHelpOptions = true)
public class EchoCommandEntry extends CommandEntry {

    public EchoCommandEntry() {
        super("Echo");
    }

    @ParentCommand
    public MasterCommonCommandNew masterCommonCommandNew;

    @Parameters(description = "The String to display")
    private String[] messages;

    @Override
    public void run() {
        if (messages != null) {
            masterCommonCommandNew.output.println(String.join(" ", messages));
        }
    }
}
