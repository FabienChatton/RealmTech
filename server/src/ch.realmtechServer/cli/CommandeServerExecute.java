package ch.realmtechServer.cli;

import ch.realmtechServer.ServerContext;
import picocli.CommandLine;

import java.io.PrintWriter;

public class CommandeServerExecute {
    private final ServerContext serverContext;

    public CommandeServerExecute(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    public void execute(String stringCommande, PrintWriter output) {
        String[] args = stringCommande.split(" ");
        MasterServerCommand masterServerCommand = new MasterServerCommand(serverContext, output);
        CommandLine commandLine = new CommandLine(masterServerCommand);
        commandLine.setErr(output);
        if (stringCommande.equals("help")) {
            commandLine.usage(output);
        } else {
            commandLine.execute(args);
        }
    }
}
