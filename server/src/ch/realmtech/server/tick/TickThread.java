package ch.realmtech.server.tick;

import ch.realmtech.server.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

public class TickThread extends Thread implements Closeable {
    private final static Logger logger = LoggerFactory.getLogger(TickThread.class);
    public final static long TIME_TICK_LAPS_MILLIS = 16;
    private final ServerContext serverContext;
    private long lastTickTime = System.currentTimeMillis();
    private boolean run = true;

    public TickThread(ServerContext serverContext) {
        super("Tick Thread");
        this.serverContext = serverContext;
    }

    @Override
    public void run() {
        while (run) {
            long deltaTime = System.currentTimeMillis() - lastTickTime;
            if (deltaTime > TIME_TICK_LAPS_MILLIS) {
                try {
                    serverContext.getEcsEngineServer().process(deltaTime / 1000f);
                    lastTickTime = System.currentTimeMillis();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    try {
                        serverContext.close();
                    } catch (InterruptedException | IOException ex) {
                        logger.error("Can not close server while an exception in tick thread has occur");
                    }
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        run = false;
    }
}
