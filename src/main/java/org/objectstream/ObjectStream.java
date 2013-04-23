package org.objectstream;


import org.objectstream.value.ListenerAdder;
import org.objectstream.value.Value;
import org.objectstream.value.ValueCalculator;
import org.objectstream.value.ValueObserver;

public interface ObjectStream {
    <T> T object(T object);
    <T> ListenerAdder addListener(ValueObserver<T> listener);
    <M> void observe(Value value, ValueObserver<M> listener);
    <M> Value<M> value(ValueCalculator<M> calculator);
    void bind(Value parent, Value child);
}
