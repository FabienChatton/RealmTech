package ch.realmtech.server.mod.commandes;

import ch.realmtech.server.mod.EvaluateAfter;
import ch.realmtech.server.registry.CommandEntry;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.Registry;
import ch.realmtech.server.registry.RegistryUtils;

import static picocli.CommandLine.Command;

@Command(name = "bonjour", description = "Dit bonjour", mixinStandardHelpOptions = true)
public class EchoSubCommand extends CommandEntry {

    private EchoCommand echoCommand;

    public EchoSubCommand() {
        super("Bonjour");
    }

    @Override
    @EvaluateAfter(classes = EchoCommand.class)
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        echoCommand = RegistryUtils.evaluateSafe(rootRegistry, EchoCommand.class);
    }

    @Override
    public void run() {
        echoCommand.alorsNon();
    }

}
