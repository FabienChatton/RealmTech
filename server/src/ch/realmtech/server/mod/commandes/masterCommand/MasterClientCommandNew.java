package ch.realmtech.server.mod.commandes.masterCommand;

import ch.realmtech.server.mod.ClientContext;

import java.io.PrintWriter;

import static picocli.CommandLine.Command;

@Command(name = "client", aliases = "c", description = "Master client command")
public class MasterClientCommandNew implements Runnable {
    public final ClientContext clientContext;
    public final PrintWriter output;

    public MasterClientCommandNew(ClientContext clientContext, PrintWriter output) {
        this.clientContext = clientContext;
        this.output = output;
    }

    @Override
    public void run() {

    }
}
