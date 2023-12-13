package ch.realmtech.server.cli;


import ch.realmtech.server.ecs.system.TimeSystem;
import ch.realmtech.server.packet.ServerResponseHandler;
import ch.realmtech.server.packet.clientPacket.TimeSetPacket;

import static picocli.CommandLine.*;

@Command(name = "set", description = "set time")
public class TimeSetCommand implements Runnable {
    @ParentCommand
    TimeCommand timeCommand;

    @Parameters(index = "0", description = "Time value")
    float time;
    @Override
    public void run() {
        TimeSystem timeSystem = timeCommand.masterServerCommand.serverContext.getSystemsAdmin().timeSystem;
        ServerResponseHandler serverHandler = timeCommand.masterServerCommand.serverContext.getServerHandler();
        timeSystem.setAccumulatedDelta(time);
        serverHandler.broadCastPacket(new TimeSetPacket(timeSystem.getAccumulatedDelta()));
    }
}
