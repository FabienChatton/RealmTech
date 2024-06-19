package ch.realmtech.server.mod.commandes.masterCommand;

import ch.realmtech.server.mod.ClientContext;

import java.io.PrintWriter;

import static picocli.CommandLine.Command;

@Command(name = "client", aliases = "c", description = "Master client command")
public class MasterClientCommandNew extends MasterCommonCommandNew implements Runnable {
    public final ClientContext clientContext;

    public MasterClientCommandNew(ClientContext clientContext, PrintWriter output) {
        super(output);
        this.clientContext = clientContext;
    }

    @Override
    public void run() {

    }
}
