package ch.realmtech.server.mod.commandes;

import ch.realmtech.server.registry.CommandEntry;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "echoSub", description = "Echo sub")
public class NewEchoSubCommand extends CommandEntry {
    public NewEchoSubCommand() {
        super("EchoSub");
    }

    @ParentCommand
    private NewEchoCommand echoCommande;

    @Override
    public void run() {
        echoCommande.oui();
    }
}
