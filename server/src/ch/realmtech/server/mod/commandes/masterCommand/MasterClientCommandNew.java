package ch.realmtech.server.mod.commandes.masterCommand;

import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommunItf;
import ch.realmtech.server.mod.ClientContext;
import ch.realmtech.server.registry.Registry;
import ch.realmtech.server.serialize.SerializerController;
import com.artemis.World;

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

    @Override
    public World getWorld() {
        return clientContext.getWorld();
    }

    @Override
    public SerializerController getSerializerController() {
        return clientContext.getSerializerController();
    }

    @Override
    public Registry<?> getRootRegistry() {
        return clientContext.getRootRegistry();
    }

    @Override
    public Context getContext() {
        return clientContext;
    }

    @Override
    public SystemsAdminCommunItf getSystemAdmin() {
        return clientContext.getSystemsAdminClient();
    }
}
