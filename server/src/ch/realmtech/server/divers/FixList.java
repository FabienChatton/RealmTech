package ch.realmtech.server.divers;

import java.util.ArrayList;

public class FixList<T> extends ArrayList<T> {
    private final int maxCapacity;

    public FixList(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public boolean add(T t) {
        boolean ret = super.add(t);
        if (size() > maxCapacity) {
            removeRange(0, size() - maxCapacity);
        }
        return ret;
    }
}
