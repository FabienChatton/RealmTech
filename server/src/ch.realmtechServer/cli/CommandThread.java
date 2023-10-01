package ch.realmtechServer.cli;

import ch.realmtechServer.ServerContext;
import picocli.CommandLine;

import java.io.Closeable;
import java.io.IOException;
import java.util.Scanner;

public class CommandThread extends Thread implements Closeable {
    private final ServerContext serverContext;
    private boolean run = true;
    private final Scanner scanner;

    public CommandThread(ServerContext serverContext) {
        super("Command Thread");
        this.serverContext = serverContext;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        while (run) {
            if (scanner.hasNextLine()) {
                String commande = scanner.next();
                MasterCommand masterCommand = new MasterCommand(serverContext);
                CommandLine commandLine = new CommandLine(masterCommand);
                if (commande.equals("help")) {
                    commandLine.usage(System.out);
                    continue;
                }
                commandLine.execute(commande);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }
    }

    @Override
    public void close() throws IOException {
        run = false;
    }
}
