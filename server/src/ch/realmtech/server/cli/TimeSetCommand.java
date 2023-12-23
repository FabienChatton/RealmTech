package ch.realmtech.server.cli;


import ch.realmtech.server.ecs.system.TimeSystem;
import ch.realmtech.server.packet.ServerResponseHandler;
import ch.realmtech.server.packet.clientPacket.TimeSetPacket;

import static picocli.CommandLine.*;

@Command(name = "set", description = "set time")
public class TimeSetCommand implements Runnable {
    @ParentCommand
    TimeCommand timeCommand;

    @Parameters(index = "0", description = "Time value. Can be an absolute time (1, 23, 84.23...) or a relative time (day, night).")
    private String time;
    @Override
    public void run() {
        TimeSystem timeSystem = timeCommand.masterServerCommand.serverContext.getSystemsAdmin().timeSystem;
        ServerResponseHandler serverHandler = timeCommand.masterServerCommand.serverContext.getServerHandler();
        float parseDelta;
        try {
            // set absolute time
             parseDelta = Float.parseFloat(time);
        } catch (NumberFormatException e) {
            float actualDelta = timeCommand.masterServerCommand.serverContext.getSystemsAdmin().timeSystem.getAccumulatedDelta();
            if (time.equals("day")) {
                float maxAlpha = 0;
                float futureDelta = actualDelta;
                for (int i = 0; i < 1200; i++) {
                    float alpha = TimeSystem.getAlpha(futureDelta + i);
                    if (alpha > maxAlpha) {
                        maxAlpha = alpha;
                        futureDelta = futureDelta + i;
                    }
                }
                parseDelta = futureDelta;
            } else if (time.equals("night")) {
                float minAlpha = 1;
                float futureDelta = actualDelta;
                for (int i = 0; i < 1200; i++) {
                    float alpha = TimeSystem.getAlpha(futureDelta + i);
                    if (alpha < minAlpha) {
                        minAlpha = alpha;
                        futureDelta = futureDelta + i;
                    }
                }
                parseDelta = futureDelta;
            } else {
                timeCommand.masterServerCommand.output.println("Can not set a relative time. Available values are: day and night.");
                return;
            }
        } catch (Exception e) {
            timeCommand.masterServerCommand.output.println("Can not set time: " + e.getMessage());
            return;
        }
        timeSystem.setAccumulatedDelta(parseDelta);
        serverHandler.broadCastPacket(new TimeSetPacket(timeSystem.getAccumulatedDelta()));
    }
}
