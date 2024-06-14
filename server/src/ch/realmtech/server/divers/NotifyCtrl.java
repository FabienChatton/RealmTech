package ch.realmtech.server.divers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NotifyCtrl {
    private final static Logger logger = LoggerFactory.getLogger(NotifyCtrl.class);
    private final Queue<Notify> gameNotifyQueue;
    private final Executor executor;
    private volatile boolean waitNotify;

    public NotifyCtrl() {
        gameNotifyQueue = new ArrayDeque<>();
        executor = Executors.newCachedThreadPool((runnable) -> {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setDaemon(true);
            return thread;
        });
    }

    public void addNotify(Notify notify) {
        synchronized (gameNotifyQueue) {
            gameNotifyQueue.add(notify);
        }
    }

    public Notify nextNotify(Runnable onNotifyTimeEnd) {
        if (waitNotify) {
            // wait to end the current notify
            return null;
        }
        Notify notify;
        synchronized (gameNotifyQueue) {
            notify = gameNotifyQueue.poll();
        }
        if (notify == null) {
            return null;
        }
        executor.execute(() -> {
            try {
                waitNotify = true;
                Thread.sleep(notify.secondeToShow() * 1000L);
                onNotifyTimeEnd.run();
            } catch (InterruptedException e) {
                logger.warn("Fail to wait a game notify", e);
            } finally {
                waitNotify = false;
            }
        });
        return notify;
    }
}
