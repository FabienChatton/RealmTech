package ch.realmtechServer.tick;

import ch.realmtechServer.ServerContext;

public class TickThread extends Thread {
    public final static long TIME_TICK_LAPS_MILLIS = 20;
    private final ServerContext serverContext;
    private long lastTickTime = System.currentTimeMillis();
    private boolean run = true;

    public TickThread(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public void run() {
        while (run) {
            long deltaTime = System.currentTimeMillis() - lastTickTime;
            if (deltaTime > TIME_TICK_LAPS_MILLIS) {
                serverContext.getEcsEngineServer().getWorld().setDelta(deltaTime);
                serverContext.getEcsEngineServer().getWorld().process();
                lastTickTime = System.currentTimeMillis();
            }
        }
    }

}
