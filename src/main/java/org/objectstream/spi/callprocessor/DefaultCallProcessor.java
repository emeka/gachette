/**
 * Copyright 2013 Emeka Mosanya, all rights reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.objectstream.spi.callprocessor;

import org.objectstream.annotations.CacheValue;
import org.objectstream.annotations.DoNotCacheValue;
import org.objectstream.annotations.InvalidateValue;
import org.objectstream.context.CallContext;
import org.objectstream.exceptions.ExceptionUtils;
import org.objectstream.instrumentation.FieldEnhancer;
import org.objectstream.instrumentation.ObjectStreamProxy;
import org.objectstream.instrumentation.ProxyFactory;
import org.objectstream.instrumentation.collections.CollectionProxy;
import org.objectstream.spi.ObjectStreamProviderHandler;
import org.objectstream.spi.graphprovider.GraphProvider;
import org.objectstream.value.MethodEvaluator;
import org.objectstream.value.Value;

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
        return getProxyFactory().createObjectProxy(targetObject, new ObjectStreamProviderHandler<>(targetObject, this));
    }

    @Override
    public void enhance(Object object) {
        getProxyFactory().enhance(object, new FieldEnhancer(this));  //createProxy
    }

    @Override
    public int calculateHashCode(Object object) {
        if (object instanceof ObjectStreamProxy) {
            return System.identityHashCode(((ObjectStreamProxy) object).getOriginalObject());
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
            Object other = parameters[0] instanceof ObjectStreamProxy ? ((ObjectStreamProxy) parameters[0]).getOriginalObject() : parameters[0];
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
                        if (!(newPropertyValue instanceof ObjectStreamProxy)) {
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
