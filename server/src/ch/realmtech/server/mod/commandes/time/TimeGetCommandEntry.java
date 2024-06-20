package ch.realmtech.server.mod.commandes.time;

import ch.realmtech.server.ecs.system.TimeSystem;
import ch.realmtech.server.registry.CommandEntry;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "get", description = "get time")
public class TimeGetCommandEntry extends CommandEntry {
    public TimeGetCommandEntry() {
        super("TimeGet");
    }

    @ParentCommand
    public TimeCommandEntry timeCommand;

    @Override
    public void run() {
        TimeSystem timeSystem = timeCommand.masterServerCommand.serverContext.getSystemsAdminServer().getTimeSystem();
        timeCommand.masterServerCommand.output.println(timeSystem.getAccumulatedDelta());
    }

}
