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

package org.gachette.instrumentation;


public abstract class AbstractProxyFactory implements ProxyFactory {
    public <T> T enhance(T object, ObjectEnhancer objectEnhancer){
        return objectEnhancer.enhance(object);
    }

    public <T> T createObjectProxy(T object, MethodHandler handler) {
        if(object instanceof GachetteProxy){
            return object;
        }
        ProxyProvider<T> pf = getProxyFactory(handler);
        return pf.create(object);
    }

    protected abstract <T> ProxyProvider<T> getProxyFactory(MethodHandler interceptor);
}
