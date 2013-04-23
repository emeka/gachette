package org.objectstream.instrumentation.javassist;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.objectstream.instrumentation.MethodInterceptor;

import java.lang.reflect.Method;

public class JavassistProxy<T> implements org.objectstream.instrumentation.ProxyFactory<T> {

    MethodInterceptor interceptor;

    public JavassistProxy(MethodInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    public T create(T obj) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(obj.getClass());
        Class<?> proxyClass = proxyFactory.createClass();
        Object wObjPK = null;
        try {
            wObjPK = proxyClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ((ProxyObject) wObjPK).setHandler(new ListenerInterceptor(interceptor));
        return (T) wObjPK;
    }

    public class ListenerInterceptor<T, M> implements MethodHandler {
        private MethodInterceptor interceptor;

        public ListenerInterceptor(MethodInterceptor interceptor) {
            this.interceptor = interceptor;
        }

        public Object invoke(Object o, Method method, Method proceed, Object[] arguments) {
            Object res = null;
            try {
                res = interceptor.intercept(o, method, arguments);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return res;
        }

    }
}
