/*
 * Copyright 2013 Emeka Mosanya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gachette.instrumentation.javassist;

import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.gachette.exceptions.ExceptionUtils;
import org.gachette.instrumentation.GachetteProxy;
import org.gachette.instrumentation.MethodHandler;
import org.gachette.instrumentation.ProxyProvider;

import java.lang.reflect.Method;

public class JavassistProxy<T> implements ProxyProvider<T> {

    MethodHandler methodHandler;

    public JavassistProxy(MethodHandler methodHandler) {
        this.methodHandler = methodHandler;
    }

    public T create(T obj) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(obj.getClass());
        proxyFactory.setInterfaces(new Class[]{GachetteProxy.class});
        Class<?> proxyClass = proxyFactory.createClass();
        Object wObjPK = null;
        try {
            wObjPK = proxyClass.newInstance();
        } catch (Throwable e) {
            throw ExceptionUtils.wrap(e);
        }
        ((ProxyObject) wObjPK).setHandler(new ListenerInterceptor(methodHandler));
        return (T) wObjPK;
    }

    private class ListenerInterceptor<T, M> implements javassist.util.proxy.MethodHandler {
        private MethodHandler interceptor;

        public ListenerInterceptor(MethodHandler interceptor) {
            this.interceptor = interceptor;
        }

        public Object invoke(Object o, Method method, Method proceed, Object[] arguments) {
            Object res = null;
            try {
                res = interceptor.handle(o, method, arguments);
            } catch (Throwable e) {
                throw ExceptionUtils.wrap(e);
            }
            return res;
        }
    }
}
