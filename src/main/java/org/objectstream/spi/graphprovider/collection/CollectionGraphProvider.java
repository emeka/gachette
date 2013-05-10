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

package org.objectstream.spi.graphprovider.collection;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.objectstream.Stream;
import org.objectstream.instrumentation.ObjectStreamProxy;
import org.objectstream.spi.graphprovider.GraphProvider;
import org.objectstream.value.Evaluator;
import org.objectstream.value.Value;
import org.objectstream.value.ValueObserver;

import java.util.*;

public class CollectionGraphProvider implements GraphProvider {
    private final Map<Integer, Map<Integer, Set<Bind>>> binds = new HashMap<>();
    private final Map<Value, Integer> objects = new HashMap<>();
    private final Map<Integer, Set<Value>> values = new HashMap<>();

    private final Set<Value> nodes = new HashSet<>();
    private final Map<Evaluator, Value> nodeMap = new HashMap<>();
    private final Map<Value, Set<ValueObserver>> nodeObservers = new HashMap<>();

    private final Map<Value, Set<Value>> nodeParents = new HashMap<>();
    private final Map<Value, Set<Value>> nodeChildren = new HashMap<>();

/*
Class<?> cl = Class.forName("javax.swing.JLabel");
Constructor<?> cons = cl.getConstructor(String.class);
Object o = cons.newInstance("JLabel");

 */

    @Override
    public <M> void observe(Value<M> value, ValueObserver<M> observer) {
        Set<ValueObserver> observers = nodeObservers.get(value);
        if (observers == null) {
            observers = new HashSet<>();
            nodeObservers.put(value, observers);
        }
        if (!observers.contains(observer)) {
            observers.add(observer);
            observer.notify(value);
        }
    }

    @Override
    public Value value(Evaluator calculator) {
        Value value = nodeMap.get(calculator);
        if (value == null) {
            value = new Value(calculator);
            nodes.add(value);
            nodeMap.put(calculator, value);
        }

        //
        Integer objectHash = calculateHashCode(calculator.targetObject());
        objects.put(value, objectHash);
        Set<Value> objectValues = values.get(objectHash);
        if (objectValues == null) {
            objectValues = new HashSet<>();
            values.put(objectHash, objectValues);
        }
        objectValues.add(value);

        return value;
    }

    @Override
    public void bind(Value parent, Value child) {
        verify(parent);
        verify(child);

        Set<Value> children = nodeChildren.get(parent);
        if (children == null) {
            children = new HashSet<>();
            nodeChildren.put(parent, children);
        }

        children.add(child);

        Set<Value> parents = nodeParents.get(child);
        if (parents == null) {
            parents = new HashSet<>();
            nodeParents.put(child, parents);
        }

        parents.add(parent);

        registerBind(parent, child);
    }

    @Override
    public void bind(Object parent, Object child) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unbind(Value parent, Value child) {
        verify(parent);
        verify(child);

        Set<Value> children = nodeChildren.get(parent);
        if (children != null) {
            children.remove(child);
        }

        Set<Value> parents = nodeParents.get(child);
        if (parents != null) {
            parents.remove(parent);
        }

        unRegisterBind(parent, child);
    }

    @Override
    public void notifyChange(Value value) {
        Set<ValueObserver> observers = nodeObservers.get(value);
        if (observers != null) {
            for (ValueObserver observer : observers) {
                observer.notify(value);
            }
        }
    }

    @Override
    public void invalidate(Value value) {
        verify(value);
        Set<Value> values = new HashSet<>();
        values.add(value);
        invalidate(values);
    }

    /**
     * This method will unbind a child object from its parent.  It uses the binds registered by the
     * <code>registerBind()</code> method.
     *
     * @param parent
     * @param child
     */
    @Override
    public void unbind(Object parent, Object child) {
        Integer parentHash = calculateHashCode(parent);
        Integer childHash = calculateHashCode(child);
        Map<Integer, Set<Bind>> children = binds.get(parentHash);
        if (children != null) {
            Set<Bind> parentChildBinds = children.get(childHash);
            if (parentChildBinds != null) {
                for (Bind bind : parentChildBinds) {
                    unbind(bind.getParent(), bind.getChild());
                    invalidate(bind.getParent());
                }
            }
        }
        if (child instanceof Collection) {
            for (Object collectionObject : ((Collection) child)) {
                unbind(parent, collectionObject);
            }
        }
    }

    /**
     * This method will invalidate every value of this object.  This is far of being optimal but it is necessary
     * when a field goes from null to a new value as no dependencies have been created from a null field.
     * This will not be necessary after implementing the object transformer with field read/write interception.
     *
     * @param object
     */
    @Override
    public void invalidate(Object object) {
        Set<Value> objectValues = values.get(calculateHashCode(object));
        if (objectValues != null) {
            for (Value value : objectValues) {
                invalidate(value);
            }
        }
    }

    private void invalidate(Set<Value> values) {
        if (values != null && !values.isEmpty()) {
            for (Value value : values) {
                value.setDirty();
                notifyChange(value);
                invalidate(nodeParents.get(value));
            }
        }
    }

    private void verify(Value value) {
        if (!nodes.contains(value)) {
            throw new RuntimeException(String.format("Unknown node:", value));
        }
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

    private void unRegisterBind(Value parent, Value child) {
        Integer parentHash = objects.get(parent);
        Integer childHash = objects.get(child);
        Map<Integer, Set<Bind>> children = binds.get(parentHash);

        if (children == null) {
            return;
        }

        Set<Bind> parentChildrenBinds = children.get(childHash);
        if (parentChildrenBinds == null) {
            return;
        }

        parentChildrenBinds.remove(new Bind(parent, child));
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

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(parent).append(child).toHashCode();
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) return true;
            if (object == null) return false;
            if (this.getClass() != object.getClass()) return false;
            Bind other = (Bind) object;
            return new EqualsBuilder().append(parent, other.parent).append(child, other.child).isEquals();
        }

    }

    private int calculateHashCode(Object object) {
        if (object instanceof ObjectStreamProxy) {
            return System.identityHashCode(((ObjectStreamProxy) object).getOriginalObject());
        } else {
            return System.identityHashCode(object);
        }
    }
}
