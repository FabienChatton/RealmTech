package ch.realmtech.server.cli;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.system.ServerCommandExecute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class CommandServerThread extends Thread implements Closeable {
    private final static Logger logger = LoggerFactory.getLogger(CommandServerThread.class);
    private final ServerContext serverContext;
    private final Scanner scanner;
    private volatile boolean run = true;

    public CommandServerThread(ServerContext serverContext) {
        super("Command Server Cli Thread");
        this.serverContext = serverContext;
        this.scanner = new Scanner(System.in);
        this.setDaemon(true);
    }

    @Override
    public void run() {
        logger.info("ready to receive cli command");
        while (run) {
            if (scanner.hasNextLine()) {
                String stringCommande = scanner.nextLine();
                PrintWriter output = new PrintWriter(System.out);
                serverContext.getSystemsAdminServer().getServerCommandExecute().execute(stringCommande, output, ServerCommandExecute.SERVER_SENDER);
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
