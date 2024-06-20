package ch.realmtech.server.mod.commandes;

import ch.realmtech.server.mod.commandes.masterCommand.MasterServerCommandNew;
import ch.realmtech.server.registry.CommandEntry;

import java.io.IOException;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "stop", description = "stop the serveur", mixinStandardHelpOptions = true)
public class StopCommandEntry extends CommandEntry {

    public StopCommandEntry() {
        super("Stop");
    }

    @ParentCommand
    private MasterServerCommandNew masterServerCommand;

    @Override
    public void run() {
        try {
            masterServerCommand.serverContext.saveAndClose();
        } catch (InterruptedException | IOException e) {
            masterServerCommand.output.println("Fail to save and close the server. Error: " + e.getMessage());
        }
    }
}
