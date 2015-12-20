package net.angrycode.daggerdemo.reactive;

public interface Observable<T> {

    void register(T observer);

    void unregister(T observer);
}