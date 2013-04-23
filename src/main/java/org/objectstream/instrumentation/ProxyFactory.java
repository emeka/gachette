package org.objectstream.instrumentation;

public interface ProxyFactory<T> {
    T create(T object);
}
