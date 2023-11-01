package ch.realmtech.game.console;


import ch.realmtech.RealmTech;
import ch.realmtechServer.cli.CommunMasterCommand;
import com.artemis.World;

import java.io.PrintWriter;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

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
}
