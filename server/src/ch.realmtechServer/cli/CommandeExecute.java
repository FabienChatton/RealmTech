package ch.realmtechServer.cli;

import picocli.CommandLine;

import java.io.PrintWriter;

public class CommandeExecute {
    private final CommandServerContext commandServerContext;

    public CommandeExecute(CommandServerContext commandServerContext) {
        this.commandServerContext = commandServerContext;
    }

    public void execute(String stringCommande, PrintWriter output) {
        String[] args = stringCommande.split(" ");
        MasterCommand masterCommand = new MasterCommand(commandServerContext, output);
        CommandLine commandLine = new CommandLine(masterCommand);
        commandLine.setErr(output);
        if (stringCommande.equals("help")) {
            commandLine.usage(output);
        } else {
            commandLine.execute(args);
        }
    }
}
