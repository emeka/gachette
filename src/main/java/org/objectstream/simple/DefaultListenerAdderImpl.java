package org.objectstream.simple;

import org.objectstream.value.ListenerAdder;
import org.objectstream.ObjectStream;
import org.objectstream.value.ValueObserver;
import org.objectstream.instrumentation.ListenerInterceptor;
import org.objectstream.instrumentation.ProxyFactory;
import org.objectstream.instrumentation.cglib.CglibProxy;

public class DefaultListenerAdderImpl<L> implements ListenerAdder {
    ObjectStream stream;
    ValueObserver<L> listener;

    public DefaultListenerAdderImpl(ObjectStream stream, ValueObserver<L> listener){
        this.stream = stream;
        this.listener = listener;
    }

    public <T> T to(T object) {
        ProxyFactory<T> pf = new CglibProxy<>(new ListenerInterceptor(object, stream, listener));
        return pf.create(object);
    }
}
