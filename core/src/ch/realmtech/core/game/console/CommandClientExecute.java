package ch.realmtech.core.game.console;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.packet.serverPacket.ConsoleCommandeRequestPacket;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.util.Arrays;

public class CommandClientExecute {
    private final RealmTech context;

    public CommandClientExecute(RealmTech context) {
        this.context = context;
    }

    public void execute(String stringCommande, PrintWriter output) {
        String[] args = stringCommande.split(" ");
        String stringCommandeWithoutContext = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (args[0].equals("s") || args[0].equals("server")) {
            context.getConnexionHandler().sendAndFlushPacketToServer(new ConsoleCommandeRequestPacket(stringCommandeWithoutContext));
        } else if (args[0].equals("c") || args[0].equals("client")) {
            MasterClientCommand masterClientCommand = new MasterClientCommand(context, output);
            CommandLine commandLine = new CommandLine(masterClientCommand);
            commandLine.setErr(output);
            commandLine.setOut(output);
            if (stringCommandeWithoutContext.equals("help")) {
                commandLine.usage(output); // auto flush
            } else {
                commandLine.execute(stringCommandeWithoutContext.split(" "));
                output.flush();
            }
        } else {
            output.println("Veuillez choisir le context, s (server) ou c (client)");
            output.flush();
        }
    }
}
