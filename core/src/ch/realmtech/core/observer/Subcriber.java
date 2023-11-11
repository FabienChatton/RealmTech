package ch.realmtech.core.observer;

public interface Subcriber<T> {
    void receive(T objectToNotify);
}
