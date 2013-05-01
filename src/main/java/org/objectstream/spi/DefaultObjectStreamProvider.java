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
import org.objectstream.instrumentation.*;
import org.objectstream.instrumentation.collections.ObjectStreamCollection;
import org.objectstream.value.MethodEvaluator;
import org.objectstream.value.Value;
import org.objectstream.value.ValueObserver;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

public class DefaultObjectStreamProvider implements ObjectStreamProvider {
    private final Map<Integer, Map<Integer, Set<Bind>>> binds = new HashMap<>();
    private final Map<Value, Integer> objects = new HashMap<>();
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
        return getProxyFactory().createObjectProxy(object, new ContextualHandler<>(object, getContext(), new EvalHandler<>(this)));
    }

    @Override
    public void enhance(Object object) {
        getProxyFactory().enhance(object, new FieldEnhancer(this));  //createProxy
    }

    @Override
    public int hashCode(Object object) {
        if (object instanceof ObjectStreamProxy) {
            return System.identityHashCode(((ObjectStreamProxy) object).getOriginalObject());
        } else {
            return System.identityHashCode(object);
        }
    }

    @Override
    public Value value(Object object, Method method, Object[] parameters) {
        Value value = streamProvider.value(new MethodEvaluator(object, method, parameters, this));
        objects.put(value, hashCode(object));

        return value;
    }

    @Override
    public <M> void observe(Value<M> value, ValueObserver<M> observer) {
        streamProvider.observe(value, observer);
    }

    @Override
    public void bind(Value parent, Value child) {
        streamProvider.bind(parent, child);

        registerBind(parent, child);
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
    public Object eval(Object object, Method method, Object[] parameters) {
        String name = method.getName();

        //We do not intercept hashCode and equals.
        if ("equals".equals(name)) {
            return object.equals(parameters[0]) ? Boolean.TRUE : Boolean.FALSE;
        } else if ("hashCode".equals(name)) {
            return object.hashCode();
        } else if ("getOriginalObject".equals(name)) {
            return object;
        }

        Object res = null;
        if (method.getReturnType() != Void.TYPE) {
            Value value = value(object, method, parameters);   //enhance
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
                Value readPropertyValue = findReadPropertyValue(object, method, parameters);
                Object oldPropertyValue = null;
                Object newPropertyValue = null;

                if (readPropertyValue != null) {
                    oldPropertyValue = readPropertyValue.eval();
                    newPropertyValue = parameters[0];
                }

                res = method.invoke(object, parameters);

                if (readPropertyValue != null && oldPropertyValue != newPropertyValue) {
                    invalidate(readPropertyValue);
                    if (oldPropertyValue != null) {
                        unbind(object, oldPropertyValue);
                    }
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
                Method write = propertyDescriptor.getWriteMethod();
                if (write != null && write.equals(method)) {
                    Method read = propertyDescriptor.getReadMethod();
                    if (read != null && read.getParameterTypes().length == 0) {
                        result = value(object, read, new Object[]{}); //enhance
                    }
                }
            }
        } catch (Throwable e) {
            throw ExceptionUtils.wrap(e);
        }
        return result;
    }

    /**
     * This implementation of the ObjectStreamProvider require to remember every binds at object level in addition
     * to value level.  This is necessary because we do not have field level dependency management (only method level).
     * If we replace the value of an Object field, we need to be able to find every binds between the parent and the
     * child objects to severe them.
     * <p/>
     * This is not necessary if we implement a class transformer that allows us to intercept field read and write.
     *
     * @param parent
     * @param child
     */
    private void registerBind(Value parent, Value child) {
        Integer parentHash = objects.get(parent);
        Integer childHash = objects.get(child);
        Map<Integer, Set<Bind>> children = binds.get(parentHash);
        if (children == null) {
            children = new HashMap<>();
            binds.put(parentHash, children);
        }

        Set<Bind> parentChildrenBinds = children.get(childHash);
        if (parentChildrenBinds == null) {
            parentChildrenBinds = new HashSet<>();
            children.put(childHash, parentChildrenBinds);
        }

        parentChildrenBinds.add(new Bind(parent, child));
    }

    /**
     * This method will unbind a child object from its parent.  It uses the binds registered by the
     * <code>registerBind()</code> method.
     *
     * @param parent
     * @param child
     */
    private void unbind(Object parent, Object child) {
        Integer parentHash = hashCode(parent);
        Integer childHash = hashCode(child);
        Map<Integer, Set<Bind>> children = binds.get(parentHash);
        if (children != null) {
            Set<Bind> parentChildBinds = children.get(childHash);
            if (parentChildBinds != null) {
                for (Bind bind : parentChildBinds) {
                    streamProvider.unbind(bind.getParent(), bind.getChild());
                    streamProvider.invalidate(bind.getParent());
                }
            }
        }
    }

    private static class Bind {
        private final Value parent;
        private final Value child;

        private Bind(Value parent, Value child) {
            this.parent = parent;
            this.child = child;
        }

        private Value getParent() {
            return parent;
        }

        private Value getChild() {
            return child;
        }
    }
}
