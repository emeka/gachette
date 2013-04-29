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

package org.objectstream.spi;

import org.objectstream.context.CallContext;
import org.objectstream.exceptions.ExceptionUtils;
import org.objectstream.instrumentation.ContextualHandler;
import org.objectstream.instrumentation.EvalHandler;
import org.objectstream.instrumentation.FieldEnhancer;
import org.objectstream.instrumentation.ProxyFactory;
import org.objectstream.value.Evaluator;
import org.objectstream.value.MethodEvaluator;
import org.objectstream.value.Value;
import org.objectstream.value.ValueObserver;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class DefaultObjectStreamProvider implements ObjectStreamProvider {
    private final ProxyFactory proxyFactory;
    private final CallContext context;
    private final StreamProvider streamProvider;

    public DefaultObjectStreamProvider(StreamProvider streamProvider, ProxyFactory proxyFactory, CallContext context) {
        this.context = context;
        this.proxyFactory = proxyFactory;
        this.streamProvider = streamProvider;
    }

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public CallContext getContext() {
        return context;
    }

    @Override
    public <T> T createProxy(T object) {                                                                      //eval
        return getProxyFactory().createObjectProxy(object,new ContextualHandler<>(object, getContext(),new EvalHandler<>(this)));
    }

    @Override
    public void enhance(Object object) {
        getProxyFactory().enhance(object, new FieldEnhancer(this));  //createProxy
    }

    @Override
    public Value value(Evaluator calculator) {
        return streamProvider.value(calculator);
    }

    @Override
    public <M> void observe(Value<M> value, ValueObserver<M> observer) {
        streamProvider.observe(value,observer);
    }

    @Override
    public void bind(Value parent, Value child) {
        streamProvider.bind(parent,child);
    }

    @Override
    public void invalidate(Value readPropertyValue) {
        streamProvider.invalidate(readPropertyValue);
    }

    @Override
    public void notifyChange(Value value) {
        streamProvider.notifyChange(value);
    }

    @Override
    public Object eval(Object object, Method method, Object[] objects) {
        Object res = null;
        if (method.getReturnType() != Void.TYPE) {
            Value value = value(new MethodEvaluator(object, method, objects, this));   //enhance
            getContext().getValueStack().push(value);
            getContext().setLastValue(value);

            Object oldValue = value.getValue();
            res = value.eval(); //this call must be between the push and the pop

            getContext().getValueStack().pop();
            if (!getContext().getValueStack().empty()) {
                bind(getContext().getValueStack().peek(), value);
            }

            if (!res.equals(oldValue)) {
                notifyChange(value);
            }

        } else {
            try {
                res = method.invoke(object, objects);
                //If it is writing a property with a primitive type, invalidate the corresponding get value.
                Value readPropertyValue = findReadPropertyValue(object, method, objects);
                if (readPropertyValue != null) {
                    invalidate(readPropertyValue);
                }
            } catch (Throwable e) {
                throw ExceptionUtils.wrap(e);
            }
        }

        return res;
    }

    private Value findReadPropertyValue(Object object, Method method, Object[] objects) {
        Value result = null;
        try {
            for (PropertyDescriptor propertyDescriptor :
                    Introspector.getBeanInfo(object.getClass(), Object.class).getPropertyDescriptors()) {
                if (propertyDescriptor.getPropertyType().isPrimitive()) {
                    Method write = propertyDescriptor.getWriteMethod();
                    if (write != null && write.equals(method)) {
                        Method read = propertyDescriptor.getReadMethod();
                        if (read != null) {
                            result = value(new MethodEvaluator(object, read, new Object[]{}, this)); //enhance
                        }
                    }
                }
            }
        } catch (Throwable e) {
            throw ExceptionUtils.wrap(e);
        }
        return result;
    }
}
