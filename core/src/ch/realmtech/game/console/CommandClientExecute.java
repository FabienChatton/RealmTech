package ch.realmtech.game.console;

import ch.realmtech.RealmTech;
import picocli.CommandLine;

import java.io.PrintWriter;

public class CommandClientExecute {
    private final RealmTech context;

    public CommandClientExecute(RealmTech context) {
        this.context = context;
    }

    public void execute(String stringCommande, PrintWriter output) {
        String[] args = stringCommande.split(" ");
        MasterClientCommand masterClientCommand = new MasterClientCommand(context, output, stringCommande);
        CommandLine commandLine = new CommandLine(masterClientCommand);
        commandLine.setErr(output);
        if (stringCommande.equals("help")) {
            commandLine.usage(output);
        } else {
            commandLine.execute(args);
        }
        output.flush();
    }
}
