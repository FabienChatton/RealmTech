package ch.realmtech.console;

import ch.realmtech.game.netty.RealmTechClientConnexionHandler;
import ch.realmtechServer.packet.serverPacket.ConsoleCommandeRequestPacket;

import java.io.Closeable;
import java.io.IOException;
import java.util.Scanner;

public class ConsoleClientInputThread extends Thread implements Closeable {
    private final RealmTechClientConnexionHandler connexionHandler;
    private volatile Scanner input;
    private volatile boolean run;
    public ConsoleClientInputThread(RealmTechClientConnexionHandler connexionHandler) {
        super("Console input Thread");
        this.connexionHandler = connexionHandler;
        this.run = true;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (run) {
            if (input == null) throw new NullPointerException("Le thread " + ConsoleClientInputThread.class.getSimpleName() + " ne peut pas etre lancer sans d'avoir d'input. Utiliser la méthode start avec l'input");
            if (input.hasNext()) {
                System.out.println(input.next());
            }

            if (input.hasNextLine()) {
                String commandeString = input.nextLine();
                connexionHandler.sendAndFlushPacketToServer(new ConsoleCommandeRequestPacket(commandeString));
            }
        }
    }

    public void start(Scanner input) {
        this.input = input;
        start();
    }

    @Override
    public synchronized void close() throws IOException {
        run = false;
    }
}
