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

import org.objectstream.context.CallContext;
import org.objectstream.exceptions.ExceptionUtils;
import org.objectstream.instrumentation.FieldEnhancer;
import org.objectstream.instrumentation.ObjectStreamProxy;
import org.objectstream.instrumentation.ProxyFactory;
import org.objectstream.instrumentation.collections.ObjectStreamCollection;
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
        if (object instanceof Collection) {
            if (object instanceof ObjectStreamCollection) {
                return object;
            } else {
                return (T) new ObjectStreamCollection((Collection) object, this, graphProvider);
            }
        }
        //eval
        return getProxyFactory().createObjectProxy(object, new ObjectStreamProviderHandler<>(object,this));
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

           //We do not intercept calculateHashCode and equals.
           if ("equals".equals(name)) {
               Object other = parameters[0] instanceof ObjectStreamProxy ? ((ObjectStreamProxy) parameters[0]).getOriginalObject() : parameters[0];
               return object.equals(other) ? Boolean.TRUE : Boolean.FALSE;
           } else if ("hashCode".equals(name)) {
               return object.hashCode();
           } else if ("getOriginalObject".equals(name)) {
               return object;
           }

           Object res = null;

           if (isValue(method)) {
               //here, the same value must be returned for the same parameters unless invalidated
               //We talking about value in the sense of functional programming (no side effects)
               Value value = graphProvider.value(new MethodEvaluator(object, method, parameters, this));   //enhance
               getContext().push(value);

               Object oldValue = value.getValue();
               res = value.eval(); //this call must be between the push and the pop

               getContext().pop();
               if (!getContext().empty()) {
                   graphProvider.bind(getContext().peek(), value);
               }

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
                           if (!(newPropertyValue instanceof ObjectStreamCollection)) {
                               newPropertyValue = createProxy(newPropertyValue);
                               parameters[0] = newPropertyValue;
                           }
                           ((ObjectStreamCollection) newPropertyValue).addParent(readPropertyValue);
                       }
                   }

                   res = method.invoke(object, parameters);
                                                     //optimisation for properties
                   if (readPropertyValue != null && oldPropertyValue != newPropertyValue) {
                       graphProvider.invalidate(readPropertyValue);
                       if (oldPropertyValue != null) {
                           //Remove oldPropertyValue dependencies and any children dependencies in case of collection
                          graphProvider.unbind(object, oldPropertyValue);
                           if (oldPropertyValue instanceof Collection) {
                               for (Object child : ((Collection) oldPropertyValue)) {
                                   graphProvider.unbind(object, child);
                               }
                               if (!getContext().empty()) {
                                   ((ObjectStreamCollection) newPropertyValue).removeParent(getContext().peek());
                               }
                           }
                       } else {
                           //If the previous value was null, we do not have any information about dependencies.  Therefore
                           //we need to blast everything.
                           graphProvider.invalidate(object);
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
                        result = graphProvider.value(new MethodEvaluator(object, read, new Object[]{}, this)); //enhance
                    }
                }
            }
        } catch (Throwable e) {
            throw ExceptionUtils.wrap(e);
        }
        return result;
    }
}
