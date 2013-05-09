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

package org.objectstream.instrumentation.collections;

import org.objectstream.instrumentation.ObjectStreamProxy;
import org.objectstream.spi.callprocessor.CallProcessor;
import org.objectstream.spi.graphprovider.GraphProvider;
import org.objectstream.value.Value;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ObjectStreamCollection<E> implements ObjectStreamProxy, Collection<E>{

    private final CallProcessor callProcessor;
    private final GraphProvider graphProvider;
    private final Collection<E> collection;
    private final Set<Value> parents = new HashSet<>();

    public ObjectStreamCollection(Collection<E> collection, CallProcessor callProcessor, GraphProvider graphProvider) {
        this.callProcessor = callProcessor;
        this.collection = findOriginalCollection(collection);
        this.graphProvider = graphProvider;
    }

    @Override
    public Collection<E> getOriginalObject() {
        return collection;
    }

    @Override
    public int size() {
        registerParentValue();
        return collection.size();
    }

    @Override
    public boolean isEmpty() {
        registerParentValue();
        return collection.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        registerParentValue();
        return collection.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        registerParentValue();
        return collection.iterator();
    }

    @Override
    public Object[] toArray() {
        registerParentValue();
        return collection.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        registerParentValue();
        return collection.toArray(a);
    }

    @Override
    public boolean add(E e) {
        boolean result = collection.add(e);
        invalidateParentValues();
        return result;
    }

    @Override
    public boolean remove(Object o) {
        boolean result = collection.remove(o);
        invalidateParentValues();
        return result;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        registerParentValue();
        return collection.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean result = collection.addAll(c);
        invalidateParentValues();
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = collection.removeAll(c);
        invalidateParentValues();
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean result = collection.retainAll(c);
        invalidateParentValues();
        return result;
    }

    @Override
    public void clear() {
        collection.clear();
        invalidateParentValues();
    }

    public void addParent(Value value){
        parents.add(value);
    }

    public void removeParent(Value value){
        parents.remove(value);
    }

    private void registerParentValue() {
        if (!callProcessor.getContext().empty()) {
            parents.add(callProcessor.getContext().peek());
        }
    }

    private void invalidateParentValues(){
        for(Value parent : parents){
            graphProvider.invalidate(parent);
        }
    }

    private Collection<E> findOriginalCollection(Collection<E> inputCollection){
        if(inputCollection instanceof ObjectStreamCollection){
            return findOriginalCollection(((ObjectStreamCollection) inputCollection).getOriginalObject());
        } else {
            return inputCollection;
        }
    }
}
