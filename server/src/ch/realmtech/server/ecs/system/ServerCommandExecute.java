package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.mod.commandes.CommandLoader;
import ch.realmtech.server.mod.commandes.MasterServerCommandSupplierEntry;
import ch.realmtech.server.mod.commandes.masterCommand.MasterServerCommandNew;
import ch.realmtech.server.registry.RegistryUtils;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import picocli.CommandLine;

import java.io.PrintWriter;

public class ServerCommandExecute extends Manager {
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    public final static int SERVER_SENDER = -2;
    private MasterServerCommandSupplierEntry masterServerCommandSupplierEntry;
    private CommandLoader commandLoader;

    @Override
    protected void initialize() {
        masterServerCommandSupplierEntry = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), MasterServerCommandSupplierEntry.class);
        commandLoader = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), CommandLoader.class);
    }

    public void execute(String stringCommande, PrintWriter output, int senderId) {
        String[] args = stringCommande.split(" ");
        MasterServerCommandNew masterServerCommandNew = masterServerCommandSupplierEntry.get(serverContext, output, senderId);

        CommandLine commandLine = new CommandLine(masterServerCommandNew);
        commandLine.setErr(output);
        commandLine.setOut(output);
        commandLoader.addServerSubCommand(serverContext.getRootRegistry(), commandLine);
//        commandLine.addSubcommand(new CommandLine(new EchoCommand())
//                .addSubcommand(new CommandLine(new EchoSubSubCommand()))
//        );
        if (stringCommande.equals("help")) {
            commandLine.usage(output);
        } else {
            commandLine.execute(args);
        }
    }
}
