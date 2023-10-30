package ch.realmtechServer.cli;

import ch.realmtechServer.ServerContext;
import picocli.CommandLine;

import java.io.PrintWriter;

public class CommandeExecute {
    private final ServerContext serverContext;

    public CommandeExecute(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    public void execute(String stringCommande, PrintWriter output) {
        MasterCommand masterCommand = new MasterCommand(serverContext, output);
        CommandLine commandLine = new CommandLine(masterCommand);
        commandLine.setErr(output);
        if (stringCommande.equals("help")) {
            commandLine.usage(output);
        } else {
            commandLine.execute(stringCommande.split(" "));
        }
    }
}
