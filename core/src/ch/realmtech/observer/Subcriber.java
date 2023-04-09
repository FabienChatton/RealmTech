package ch.realmtech.observer;

public interface Subcriber<T> {
    void receive(T objectToNotify);
}
