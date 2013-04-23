package org.objectstream.instrumentation;


import org.objectstream.value.MethodValue;
import org.objectstream.value.Value;
import org.objectstream.ObjectStream;
import org.objectstream.transaction.DependencyContext;

import java.lang.reflect.Method;

public class ObjectInterceptor<T, M> implements MethodInterceptor {
    private ObjectStream stream;
    private T realObj;

    public ObjectInterceptor(T realObj, ObjectStream stream) {
        this.realObj = realObj;
        this.stream = stream;
    }

    public Object intercept(Object o, Method method, Object[] objects) {

        Object res = null;
        if (method.getReturnType() != Void.TYPE) {
            Value value = stream.value(new MethodValue(realObj, method, objects));
            DependencyContext.push(value);

            res = value.getValue();

            DependencyContext.pop();
            if (! DependencyContext.empty()) {
                stream.bind(DependencyContext.top(), value);
            }
        } else {
            try {
                res = method.invoke(realObj, objects);
            } catch (Throwable e) {
                new RuntimeException(e);
            }
        }

        return res;
    }
}
