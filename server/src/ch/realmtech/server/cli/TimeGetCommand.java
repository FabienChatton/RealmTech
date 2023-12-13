package ch.realmtech.server.cli;

import ch.realmtech.server.ecs.system.TimeSystem;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "get", description = "get time")
public class TimeGetCommand implements Runnable {
    @ParentCommand
    TimeCommand timeCommand;
    @Override
    public void run() {
        TimeSystem timeSystem = timeCommand.masterServerCommand.serverContext.getSystemsAdmin().timeSystem;
        timeCommand.masterServerCommand.output.println(timeSystem.getAccumulatedDelta());
    }
}
