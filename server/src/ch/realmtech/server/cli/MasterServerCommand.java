package ch.realmtech.server.cli;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.registry.Registry;
import ch.realmtech.server.serialize.SerializerController;
import com.artemis.World;

import java.io.PrintWriter;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;

@Command(name = "server", aliases = "s", subcommands = {
        StopCommand.class,
        TeleportPlayerCommand.class,
        GiveCommand.class,
        TimeCommand.class,
        NotifyServerCommand.class,
        // SummonCommand.class,
})
public class MasterServerCommand extends CommunMasterCommand implements Callable<Integer> {
    final ServerContext serverContext;
    final int senderId;

    public MasterServerCommand(ServerContext serverContext, PrintWriter output, int senderId) {
        super(output);
        this.serverContext = serverContext;
        this.senderId = senderId;
    }

    @Override
    public Integer call() throws Exception {
        return 0;
    }

    @Override
    public World getWorld() {
        return serverContext.getEcsEngineServer().getWorld();
    }

    @Override
    public SerializerController getSerializerManagerController() {
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
    public SystemsAdminCommun getSystemAdmin() {
        return serverContext.getSystemsAdminServer();
    }

    public String getSenderString() {
        String senderString;
        if (senderId == CommandeServerExecute.SERVER_SENDER) {
            senderString = "[SERVER]";
        } else {
            senderString = serverContext.getSystemsAdminServer().getPlayerManagerServer().getPlayerConnexionComponentById(senderId).getUsername();
        }

        return senderString;
    }
}
