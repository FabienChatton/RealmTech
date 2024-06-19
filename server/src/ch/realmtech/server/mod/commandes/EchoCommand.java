package ch.realmtech.server.mod.commandes;

import ch.realmtech.server.mod.commandes.masterCommand.MasterServerCommandNew;
import ch.realmtech.server.registry.CommandEntry;

import static picocli.CommandLine.*;

@Command(name = "echo", description = "Display a line of text.", mixinStandardHelpOptions = true)
public class EchoCommand extends CommandEntry {
    @ParentCommand
    private MasterServerCommandNew masterServerCommand;

    @Parameters
    private String[] message;

    public EchoCommand() {
        super("Echo");
    }

    @Override
    public void run() {
        if (message != null) {
            masterServerCommand.output.println(String.join(" ", message));
        }
    }

    public void alorsNon() {
        System.out.println("alors non");
    }
}
