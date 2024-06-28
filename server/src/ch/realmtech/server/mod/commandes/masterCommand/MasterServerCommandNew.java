package ch.realmtech.server.mod.commandes.masterCommand;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.ecs.system.ServerCommandExecute;
import ch.realmtech.server.registry.Registry;
import ch.realmtech.server.serialize.SerializerController;
import com.artemis.World;

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

    @Override
    public World getWorld() {
        return serverContext.getEcsEngineServer().getWorld();
    }

    @Override
    public SerializerController getSerializerController() {
        return serverContext.getSerializerController();
    }

    @Override
    public Registry<?> getRootRegistry() {
        return serverContext.getRootRegistry();
    }

    @Override
    public Context getContext() {
        return serverContext;
    }

    @Override
    public SystemsAdminServer getSystemAdmin() {
        return serverContext.getSystemsAdminServer();
    }

    public String getSenderString() {
        String senderString;
        if (senderId == ServerCommandExecute.SERVER_SENDER) {
            senderString = "[SERVER]";
        } else {
            senderString = serverContext.getSystemsAdminServer().getPlayerManagerServer().getPlayerConnexionComponentById(senderId).getUsername();
        }

        return senderString;
    }
}
