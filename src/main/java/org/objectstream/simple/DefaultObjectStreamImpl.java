package org.objectstream.simple;


import org.objectstream.ObjectStream;
import org.objectstream.value.*;
import org.objectstream.instrumentation.ObjectInterceptor;
import org.objectstream.instrumentation.ProxyFactory;
import org.objectstream.instrumentation.cglib.CglibProxy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultObjectStreamImpl implements ObjectStream {
    private final Map<ValueCalculator, Value> nodes = new HashMap<>();
    private final Map<Value, Set<ValueObserver>> nodeListeners = new HashMap<>();
    private final Map<Value,Set<Value>> nodeParents = new HashMap<>();
    private final Map<Value,Set<Value>> nodeChildren = new HashMap<>();

/*
Class<?> cl = Class.forName("javax.swing.JLabel");
Constructor<?> cons = cl.getConstructor(String.class);
Object o = cons.newInstance("JLabel");

 */

    @Override
    public <T> T object(T object) {
        ProxyFactory<T> pf = new CglibProxy<>(new ObjectInterceptor(object, this));
        return pf.create(object);
    }

    @Override
    public <T> ListenerAdder addListener(ValueObserver<T> listener) {
        return new DefaultListenerAdderImpl(this,listener);
    }

    @Override
    public <L> void observe(Value value, ValueObserver<L> listener) {
        if(!nodeListeners.containsKey(value)){
            Set listeners = new HashSet <>();
            nodeListeners.put(value, listeners);
        }

        Set<ValueObserver> listeners = nodeListeners.get(value);
        listeners.add(listener);
    }

    @Override
    public <M> Value<M> value(ValueCalculator<M> calculator) {
        Value<M> value = nodes.get(calculator);
        if(value == null){
            value = new Value(calculator);
            nodes.put(calculator, value);
        }

        return value;
    }

    @Override
    public void bind(Value parent, Value child) {
        Set<Value> children = nodeChildren.get(parent);
        if(children == null){
            children = new HashSet<>();
            nodeChildren.put(parent,children);
        }

        children.add(child);

        Set<Value> parents = nodeParents.get(child);
        if(parents == null){
            parents = new HashSet<>();
            nodeParents.put(child, parents);
        }

        parents.add(parent);
    }
}
