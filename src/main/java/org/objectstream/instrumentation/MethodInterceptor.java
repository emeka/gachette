package org.objectstream.instrumentation;

import java.lang.reflect.Method;

public interface MethodInterceptor {
    Object intercept(Object o, Method method, Object[] objects);
}
