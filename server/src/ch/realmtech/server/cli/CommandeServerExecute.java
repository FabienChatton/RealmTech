package ch.realmtech.server.cli;

import ch.realmtech.server.ServerContext;
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
        commandLine.setOut(output);
        if (stringCommande.equals("help")) {
            commandLine.usage(output);
        } else {
            commandLine.execute(args);
        }
    }
}
