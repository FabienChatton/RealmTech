package ch.realmtech.server.mod.commandes.masterCommand;

import ch.realmtech.server.ServerContext;

import java.io.PrintWriter;

import static picocli.CommandLine.Command;

@Command(name = "server", aliases = "s", description = "Master server command new.")
public class MasterServerCommandNew extends MasterCommonCommandNew implements Runnable {
    public final ServerContext serverContext;
    public final int senderId;

    public MasterServerCommandNew(ServerContext serverContext, PrintWriter output, int senderId) {
        super(output);
        this.serverContext = serverContext;
        this.senderId = senderId;
    }

    @Override
    public void run() {

    }
}
