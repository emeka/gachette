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

package org.gachette.instrumentation.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;
import org.gachette.exceptions.ExceptionUtils;
import org.gachette.instrumentation.GachetteProxy;
import org.gachette.instrumentation.MethodHandler;
import org.gachette.instrumentation.ProxyProvider;

import java.lang.reflect.Method;

public class CglibProxy<T>  implements ProxyProvider<T> {

    MethodHandler methodHandler;

    public CglibProxy(MethodHandler methodHandler) {
        this.methodHandler = methodHandler;
    }

    public T create(T obj) {
        Enhancer e = new Enhancer();
        e.setSuperclass(obj.getClass());
        e.setCallback(new ListenerInterceptor(methodHandler));
        e.setInterfaces(new Class[]{GachetteProxy.class});
        return (T) e.create();
    }

    private class ListenerInterceptor<T, M> implements net.sf.cglib.proxy.MethodInterceptor {
        private MethodHandler interceptor;

        public ListenerInterceptor(MethodHandler interceptor) {
            this.interceptor = interceptor;
        }

        public Object intercept(Object o, Method method, Object[] arguments, MethodProxy methodProxy) {
            Object res;
            try {
                res = interceptor.handle(o, method, arguments);
            } catch (Throwable e) {
                throw ExceptionUtils.wrap(e);
            }

            return res;
        }
    }
}

//http://in.relation.to/Bloggers/DeprecatedCGLIBSupport
//http://www.theserverside.com/news/1363571/Remote-Lazy-Loading-in-Hibernate
