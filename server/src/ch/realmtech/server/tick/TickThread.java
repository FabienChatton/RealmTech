package ch.realmtech.server.tick;

import ch.realmtech.server.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

public class TickThread extends Thread implements Closeable {
    private final static Logger logger = LoggerFactory.getLogger(TickThread.class);
    public final static int TIME_TICK_LAPS_MILLIS = 16;
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
                        serverContext.saveAndClose();
                    } catch (Exception ex) {
                        serverContext.getClientInternalConnexion().ifPresent((internalConnexion) -> internalConnexion.displayErrorPopup("Can not save server while an exception in tick thread has occur"));
                        try {
                            serverContext.close();
                        } catch (Exception exc) {
                            serverContext.getClientInternalConnexion().ifPresent((internalConnexion) -> internalConnexion.displayErrorPopup("Can not close server."));
                        }
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
