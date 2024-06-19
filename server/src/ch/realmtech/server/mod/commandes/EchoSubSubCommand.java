package ch.realmtech.server.mod.commandes;

import ch.realmtech.server.mod.EvaluateAfter;
import ch.realmtech.server.registry.CommandEntry;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.Registry;

import static picocli.CommandLine.Command;

@Command(name = "alors", description = "encore")
public class EchoSubSubCommand extends CommandEntry {
    public EchoSubSubCommand() {
        super("SubSub");
    }

    @Override
    @EvaluateAfter(classes = EchoSubCommand.class)
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        super.evaluate(rootRegistry);
    }

    @Override
    public void run() {
        System.out.println("a");
    }
}
