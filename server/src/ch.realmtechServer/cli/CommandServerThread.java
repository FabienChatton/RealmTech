package ch.realmtechServer.cli;

import ch.realmtechServer.ServerContext;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class CommandServerThread extends Thread implements Closeable {
    private final ServerContext serverContext;
    private final Scanner scanner;
    private volatile boolean run = true;
    private final CommandeExecute commandeExecute;

    public CommandServerThread(ServerContext serverContext, CommandeExecute commandeExecute) {
        super("Command Server Cli Thread");
        this.serverContext = serverContext;
        this.scanner = new Scanner(System.in);
        this.commandeExecute = commandeExecute;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (run) {
            if (scanner.hasNextLine()) {
                String stringCommande = scanner.nextLine();
                PrintWriter output = new PrintWriter(System.out);
                commandeExecute.execute(stringCommande, output);
                output.flush();
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
