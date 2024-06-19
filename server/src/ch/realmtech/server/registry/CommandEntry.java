package ch.realmtech.server.registry;

import ch.realmtech.server.mod.commandes.masterCommand.MasterClientCommandNew;
import ch.realmtech.server.mod.commandes.masterCommand.MasterCommonCommandNew;
import ch.realmtech.server.mod.commandes.masterCommand.MasterServerCommandNew;

/**
 * Command entry are command executed by the user for a context.
 * The command entry is also the command, so the entry class must be annotated with {@link picocli.CommandLine.Command}.
 * The command must hava a parent command. There is 4 possibles parents:
 * <ul>
 *     <li>{@link MasterServerCommandNew} -&gt; only on server</li>
 *     <li>{@link MasterClientCommandNew} -&gt; only on client</li>
 *     <li>{@link MasterCommonCommandNew} -&gt; server and client</li>
 *     <li>A other {@link CommandEntry} -&gt; depends on parent</li>
 * </ul>
 * The {@link picocli.CommandLine.ParentCommand} annotation must be on a field with those type.
 * <br>
 * Exemple:
 * <pre>
 * {@code
 * @Command(name = "test", description = "my test command")
 * public class NewEchoSubCommand extends CommandEntry {
 *     public NewEchoSubCommand() {
 *         super("Test");
 *     }
 *
 *     @ParentCommand
 *     private MasterServerCommand masterServerCommand;
 *
 *     @Override
 *     public void run() {
 *         masterCommonCommand.output.println("hello my test command from server");
 *     }
 * }
 * }
 * </pre>
 * Here the {@link picocli.CommandLine.ParentCommand} is used with the field {@link MasterClientCommandNew}, so
 * the command is available in the server context.
 * <p>
 * The command is executed in the {@link CommandEntry#run()} methode.
 * Use the output from parent command for output message.
 */
public abstract class CommandEntry extends Entry implements Runnable {

    public CommandEntry(String name) {
        super(name);
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {

    }
}
