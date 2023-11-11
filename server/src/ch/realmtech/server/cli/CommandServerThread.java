package ch.realmtech.server.cli;

import ch.realmtech.server.ServerContext;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class CommandServerThread extends Thread implements Closeable {
    private final ServerContext serverContext;
    private final Scanner scanner;
    private volatile boolean run = true;
    private final CommandeServerExecute commandeServerExecute;

    public CommandServerThread(ServerContext serverContext, CommandeServerExecute commandeServerExecute) {
        super("Command Server Cli Thread");
        this.serverContext = serverContext;
        this.scanner = new Scanner(System.in);
        this.commandeServerExecute = commandeServerExecute;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        System.out.println("ready to receive cli command");
        while (run) {
            if (scanner.hasNextLine()) {
                String stringCommande = scanner.nextLine();
                PrintWriter output = new PrintWriter(System.out);
                commandeServerExecute.execute(stringCommande, output);
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
