package ch.realmtech.observer;

import com.artemis.utils.Bag;
import com.badlogic.gdx.utils.Array;

public class Observer<T> {
    private Bag<Subcriber<T>> subscribers;

    public Observer() {
        subscribers = new Bag<>();
    }

    public void add(Subcriber<T> subcriber) {
        subscribers.add(subcriber);
    }

    public void remove(Subcriber<T> subcriber) {
        subscribers.remove(subcriber);
    }

    public void notifySubscribers(T objectToNotify){
        for (Subcriber<T> subscriber : subscribers) {
            subscriber.receive(objectToNotify);
        }
    }
}
