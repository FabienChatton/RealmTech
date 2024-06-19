package ch.realmtech.server.registry;

import ch.realmtech.server.mod.EvaluateAfter;
import ch.realmtech.server.mod.commandes.masterCommand.MasterClientCommandNew;
import ch.realmtech.server.mod.commandes.masterCommand.MasterServerCommandNew;
import com.artemis.annotations.UnstableApi;
import picocli.CommandLine.ParentCommand;

/**
 * Command entry are command executed by the user for a context.
 * Command must have a parent command. If the parent command is the master command of the context
 * (masterServerCommand or masterClientCommand), the parent must be declared has a field and have the
 * {@link ParentCommand}. The field must have the parent type depending on the context,
 * {@link MasterServerCommandNew} or {@link MasterClientCommandNew}. Here the command will receive the
 * {@link MasterServerCommandNew}
 * <pre>
 * {@code
 *     @ParentCommand
 *     private MasterServerCommandNew masterServerCommand;
 * }
 * </pre>
 *
 *
 * <p>
 * If the parent is a command entry, put the parent command class in the {@link EvaluateAfter} annotation, here
 * the parent command is echo:
 * <pre>
 * {@code
 *     @Override
 *     @EvaluateAfter(classes = EchoCommand.class)
 *     public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
 *         echoCommand = RegistryUtils.evaluateSafe(rootRegistry, EchoCommand.class);
 *     }
 * }
 * </pre>
 * <p>
 * Commands can be executed on client side or server side.<br>
 * For a command to be executed on client side, the command entry must be in a registry with the tag {@code #clientCommands}<br>
 * For a command to be executed on server side, the command entry must be in a registry with the tag {@code #serverCommands}<br>
 * Here an exemple for registry server commands.
 * <pre>
 * {@code
 *  // server commands
 *  Registry<CommandEntry> commandServerRegistry = Registry.createRegistry(commandRegistry, "server", "customCommands", "serverCommands");
 *  commandServerRegistry.addEntry(new EchoCommand());
 * }
 * </pre>
 */
@UnstableApi
public abstract class CommandEntry extends Entry implements Runnable {

    public CommandEntry(String name) {
        super(name);
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {

    }
}
