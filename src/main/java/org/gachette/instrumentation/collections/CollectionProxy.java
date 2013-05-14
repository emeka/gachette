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

package org.gachette.instrumentation.collections;

import org.gachette.annotations.CacheValue;
import org.gachette.annotations.DoNotCacheValue;
import org.gachette.annotations.InvalidateValue;
import org.gachette.annotations.Volatile;

import java.util.Collection;
import java.util.Iterator;

public class CollectionProxy<E> implements Collection<E>{
    private Collection<E> collection;


    public void setCollection(Collection<E> collection) {
        this.collection = findOriginalCollection(collection);
    }

    @DoNotCacheValue
    public Collection<E> getCollection() {
        return collection;
    }

    @Override
    @CacheValue
    public int size() {
        return collection.size();
    }

    @Override
    @CacheValue
    public boolean isEmpty() {
        return collection.isEmpty();
    }

    @Override
    @CacheValue
    public boolean contains(Object o) {
        return collection.contains(o);
    }

    @Override
    @CacheValue
    @Volatile
    public Iterator<E> iterator() {
        return collection.iterator();
    }

    @Override
    @CacheValue
    public Object[] toArray() {
        return collection.toArray();
    }

    @Override
    @CacheValue
    public <T> T[] toArray(T[] a) {
        return collection.toArray(a);
    }

    @Override
    @InvalidateValue
    public boolean add(E e) {
        return collection.add(e);
    }

    @Override
    @InvalidateValue
    public boolean remove(Object o) {
        return collection.remove(o);
    }

    @Override
    @CacheValue
    public boolean containsAll(Collection<?> c) {
        return collection.containsAll(c);
    }

    @Override
    @InvalidateValue
    public boolean addAll(Collection<? extends E> c) {
        return collection.addAll(c);
    }

    @Override
    @InvalidateValue
    public boolean removeAll(Collection<?> c) {
        return collection.removeAll(c);
    }

    @Override
    @InvalidateValue
    public boolean retainAll(Collection<?> c) {
        return collection.retainAll(c);
    }

    @Override
    @InvalidateValue
    public void clear() {
        collection.clear();
    }

    @Override
    public int hashCode(){
        return collection.hashCode();
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof Collection)){
            return false;
        }
        return collection.equals(findOriginalCollection((Collection<E>)other));
    }

    private Collection<E> findOriginalCollection(Collection<E> inputCollection){
        if(inputCollection instanceof CollectionProxy){
            return findOriginalCollection(((CollectionProxy) inputCollection).getCollection());
        } else {
            return inputCollection;
        }
    }
}
