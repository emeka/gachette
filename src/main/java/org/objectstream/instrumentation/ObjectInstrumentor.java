package org.objectstream.instrumentation;


public interface ObjectInstrumentor<T> {
    T enhance(T object);
}
