package ch.realmtech.core.game.console;


import ch.realmtech.core.RealmTech;
import ch.realmtech.server.cli.CommunMasterCommand;
import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.registry.Registry;
import ch.realmtech.server.serialize.SerializerController;
import com.artemis.World;

import java.io.PrintWriter;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;

@Command(name = "client", aliases = "c", subcommands = {

}, description = "command client")
public class MasterClientCommand extends CommunMasterCommand implements Callable<Integer> {

    final RealmTech context;

    public MasterClientCommand(RealmTech context, PrintWriter output) {
        super(output);
        this.context = context;
    }

    @Override
    public Integer call() throws Exception {
        return 0;
    }

    @Override
    public World getWorld() {
        return context.getEcsEngine().getWorld();
    }

    @Override
    public SerializerController getSerializerManagerController() {
        return context.getSerializerController();
    }

    @Override
    public Registry<?> getRootRegistry() {
        return context.getRootRegistry();
    }

    @Override
    public Context getContext() {
        return context;
    }
}
