package ch.realmtech.core.game.divers;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.divers.Notify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Queue;

public class NotifyCtrl {
    private final static Logger logger = LoggerFactory.getLogger(NotifyCtrl.class);
    private final RealmTech context;
    private final Queue<Notify> gameNotifyQueue;
    private volatile boolean waitNotify;

    public NotifyCtrl(RealmTech context) {
        gameNotifyQueue = new ArrayDeque<>();
        this.context = context;
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

        waitNotify = true;
        context.nextTickSimulation(notify.secondeToShow() * 60, () -> {
            onNotifyTimeEnd.run();
            waitNotify = false;
        });
        return notify;
    }
}
