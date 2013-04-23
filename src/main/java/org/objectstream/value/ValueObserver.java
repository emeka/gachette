package org.objectstream.value;

public interface ValueObserver<T> {
    void update(T value);
}
