package ch.realmtech.observer;

import com.badlogic.gdx.utils.Array;

public class Observer<T> {
    private Array<Subcriber<T>> subscribers;

    public Observer() {
        subscribers = new Array<>();
    }

    public void add(Subcriber<T> subcriber) {
        subscribers.add(subcriber);
    }

    public void remove(Subcriber<T> subcriber) {
        subscribers.removeValue(subcriber, true);
    }

    public void notifySubscribers(T objectToNotify){
        for (Subcriber<T> subscriber : subscribers) {
            subscriber.receive(objectToNotify);
        }
    }
}
