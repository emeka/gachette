package org.objectstream.instrumentation;


import org.objectstream.value.MethodValue;
import org.objectstream.value.Value;
import org.objectstream.ObjectStream;
import org.objectstream.value.ValueObserver;

import java.lang.reflect.Method;

public class ListenerInterceptor<T,M> implements MethodInterceptor {
    private ObjectStream stream;
    private T realObj;
    private ValueObserver<M> listener;

    public ListenerInterceptor(T realObj, ObjectStream stream, ValueObserver<M> listener) {
        this.realObj = realObj;
        this.stream = stream;
        this.listener = listener;
    }

    public Object intercept(Object o, Method method, Object[] objects) {
        stream.observe(new Value(new MethodValue(realObj, method, objects)), listener);
        return null;
    }
}
