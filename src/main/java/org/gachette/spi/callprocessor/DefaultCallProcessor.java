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

package org.gachette.spi.callprocessor;

import org.gachette.annotations.CacheValue;
import org.gachette.annotations.DoNotCacheValue;
import org.gachette.annotations.InvalidateValue;
import org.gachette.context.CallContext;
import org.gachette.exceptions.ExceptionUtils;
import org.gachette.instrumentation.FieldEnhancer;
import org.gachette.instrumentation.GachetteProxy;
import org.gachette.instrumentation.ProxyFactory;
import org.gachette.instrumentation.collections.CollectionProxy;
import org.gachette.spi.CallProcessorMethodHandler;
import org.gachette.spi.graphprovider.GraphProvider;
import org.gachette.value.MethodEvaluator;
import org.gachette.value.Value;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;

public class DefaultCallProcessor implements CallProcessor {
    private final GraphProvider graphProvider;
    private final ProxyFactory proxyFactory;
    private final CallContext context;

    public DefaultCallProcessor(GraphProvider graphProvider, ProxyFactory proxyFactory, CallContext context) {
        this.graphProvider = graphProvider;
        this.proxyFactory = proxyFactory;
        this.context = context;
    }

    @Override
    public <T> T createProxy(T object) {
        T targetObject = object;
        if (object instanceof Collection && !(object instanceof CollectionProxy)) {
            targetObject = (T) new CollectionProxy<>();
            ((CollectionProxy) targetObject).setCollection((Collection) object);
        }
        return getProxyFactory().createObjectProxy(targetObject, new CallProcessorMethodHandler<>(targetObject, this));
    }

    @Override
    public void enhance(Object object) {
        getProxyFactory().enhance(object, new FieldEnhancer(this));  //createProxy
    }

    @Override
    public int calculateHashCode(Object object) {
        if (object instanceof GachetteProxy) {
            return System.identityHashCode(((GachetteProxy) object).getOriginalObject());
        } else {
            return System.identityHashCode(object);
        }
    }

    @Override
    public Object eval(Object object, Method method, Object[] parameters) {
        String name = method.getName();

        //The order of the tests below is relevant.
        if ("getOriginalObject".equals(name)) {
            return object;
        } else if (method.getAnnotation(DoNotCacheValue.class) != null) {
            try {
                return method.invoke(object, parameters);
            } catch (Throwable e) {
                throw ExceptionUtils.wrap(e);
            }
        } else if ("equals".equals(name)) {
            Object other = parameters[0] instanceof GachetteProxy ? ((GachetteProxy) parameters[0]).getOriginalObject() : parameters[0];
            return object.equals(other) ? Boolean.TRUE : Boolean.FALSE;
        } else if ("hashCode".equals(name)) {
            return object.hashCode();
        }

        Object res = null;

        if (isValue(method)) {
            //here, the same value must be returned for the same parameters unless invalidated
            //We talking about value in the sense of functional programming (no side effects)
            Value value = graphProvider.value(new MethodEvaluator(object, method, parameters, this));   //enhance
            getContext().push(value);

            //TODO: add a value.will
            Object oldValue = value.getValue();
            res = value.eval(); //this call must be between the push and the pop

            getContext().pop();
            if (!getContext().empty()) {
                graphProvider.bind(getContext().peek(), value);
            }

            //TODO: This should be handled by the value or the graph
            if (!res.equals(oldValue)) {
                graphProvider.notifyChange(value);
            }

        } else {
            //Here are the method call with side effects.
            try {
                //This is a special case of "findDependentValues"
                Value readPropertyValue = findReadPropertyValue(object, method, parameters);
                Object oldPropertyValue = null;
                Object newPropertyValue = null;

                if (readPropertyValue != null) {
                    oldPropertyValue = readPropertyValue.eval();
                    newPropertyValue = parameters[0];
                    if (newPropertyValue != null && newPropertyValue instanceof Collection) {
                        if (!(newPropertyValue instanceof GachetteProxy)) {
                            newPropertyValue = createProxy(newPropertyValue);
                            parameters[0] = newPropertyValue;
                        }
                    }
                }

                res = method.invoke(object, parameters);
                //optimisation for properties
                if (method.getAnnotation(InvalidateValue.class) != null) {
                    graphProvider.invalidate(object);
                } else {
                    if (readPropertyValue != null && oldPropertyValue != newPropertyValue) {
                        graphProvider.invalidate(readPropertyValue);
                        if (oldPropertyValue != null) {
                            //Remove oldPropertyValue dependencies and any children dependencies in case of collection
                            graphProvider.unbind(object, oldPropertyValue);
                        } else {
                            //If the previous value was null, we do not have any information about dependencies.  Therefore
                            //we need to blast everything.
                            graphProvider.invalidate(object);
                        }
                    }
                }
            } catch (Throwable e) {
                throw ExceptionUtils.wrap(e);
            }
        }

        return res;
    }

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public CallContext getContext() {
        return context;
    }

    private boolean isValue(Method method) {
        if (method.getAnnotation(CacheValue.class) != null) {
            return true;
        }
        if (method.getAnnotation(InvalidateValue.class) != null) {
            return false;
        }
        return method.getReturnType() != Void.TYPE;
    }

    private Value findReadPropertyValue(Object object, Method method, Object[] objects) {
        Value result = null;
        try {
            for (PropertyDescriptor propertyDescriptor :
                    Introspector.getBeanInfo(object.getClass(), Object.class).getPropertyDescriptors()) {
                Method write = propertyDescriptor.getWriteMethod();
                if (write != null && write.equals(method)) {
                    Method read = propertyDescriptor.getReadMethod();
                    if (read != null && read.getParameterTypes().length == 0) {
                        result = graphProvider.value(new MethodEvaluator(object, read, null, this)); //enhance
                    }
                }
            }
        } catch (Throwable e) {
            throw ExceptionUtils.wrap(e);
        }
        return result;
    }
}
