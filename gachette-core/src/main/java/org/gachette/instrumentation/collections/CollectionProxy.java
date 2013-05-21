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
