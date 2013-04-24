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

package org.objectstream.spi.simple;


import org.objectstream.instrumentation.ProxyFactory;
import org.objectstream.spi.ObjectStreamProvider;
import org.objectstream.value.Value;
import org.objectstream.value.ValueCalculator;
import org.objectstream.value.ValueObserver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultObjectStreamProvider implements ObjectStreamProvider {
    private final Map<ValueCalculator, Value> nodes = new HashMap<>();
    private final Map<Value, Set<ValueObserver>> nodeListeners = new HashMap<>();
    private final Map<Value,Set<Value>> nodeParents = new HashMap<>();
    private final Map<Value,Set<Value>> nodeChildren = new HashMap<>();

    private ProxyFactory proxyFactory;

/*
Class<?> cl = Class.forName("javax.swing.JLabel");
Constructor<?> cons = cl.getConstructor(String.class);
Object o = cons.newInstance("JLabel");

 */

    @Override
    public <M> void observe(Value<M> value, ValueObserver<M> observer) {
        if(!nodeListeners.containsKey(value)){
            Set listeners = new HashSet <>();
            nodeListeners.put(value, listeners);
        }

        Set<ValueObserver> listeners = nodeListeners.get(value);
        listeners.add(observer);
    }

    @Override
    public <M> Value<M> value(ValueCalculator<M> calculator) {
        Value<M> value = nodes.get(calculator);
        if(value == null){
            value = new Value(calculator);
            nodes.put(calculator, value);
        }

        return value;
    }

    @Override
    public void bind(Value parent, Value child) {
        Set<Value> children = nodeChildren.get(parent);
        if(children == null){
            children = new HashSet<>();
            nodeChildren.put(parent,children);
        }

        children.add(child);

        Set<Value> parents = nodeParents.get(child);
        if(parents == null){
            parents = new HashSet<>();
            nodeParents.put(child, parents);
        }

        parents.add(parent);
    }

    public void setProxyFactory(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }
}
