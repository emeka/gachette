package org.objectstream.instrumentation.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.transform.impl.InterceptFieldEnabled;
import org.objectstream.instrumentation.MethodInterceptor;
import org.objectstream.instrumentation.ObjectStreamProxy;
import org.objectstream.instrumentation.ProxyFactory;

import java.lang.reflect.Method;

public class CglibProxy<T> {

    MethodInterceptor interceptor;

    public CglibProxy(MethodInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    public T create(T obj) {
        Enhancer e = new Enhancer();
        e.setSuperclass(obj.getClass());
        e.setCallback(new ListenerInterceptor(interceptor));
        e.setInterfaces(new Class[]{InterceptFieldEnabled.class, ObjectStreamProxy.class});
        return (T) e.create();
    }

    public class ListenerInterceptor<T, M> implements net.sf.cglib.proxy.MethodInterceptor {
        private MethodInterceptor interceptor;

        public ListenerInterceptor(MethodInterceptor interceptor) {
            this.interceptor = interceptor;
        }

        public Object intercept(Object o, Method method, Object[] arguments, MethodProxy methodProxy) {
            Object res;
            try {
                res = interceptor.intercept(o, method, arguments);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }

            return res;
        }
    }
}

//http://in.relation.to/Bloggers/DeprecatedCGLIBSupport
//http://www.theserverside.com/news/1363571/Remote-Lazy-Loading-in-Hibernate
