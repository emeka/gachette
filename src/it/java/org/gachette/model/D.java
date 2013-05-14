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

package org.gachette.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;

public class D {
    private Collection<A> collection;
    private long value;

    public void setValue(long value) {
        this.value = value;
    }

    public long getValue() {
        return this.value;
    }

    public long getResult() {
        long result = value;
        if (collection != null) {
            for (A a : collection) {
                result += a.getResult();
            }
        }
        return result;
    }

    public Collection<A> getCollection() {
        return collection;
    }

    public void setCollection(Collection<A> collection) {
        this.collection = collection;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(value).append(collection).toHashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) return true;
        if (object == null) return false;
        if (this.getClass() != object.getClass()) return false;
        D other = (D) object;
        return new EqualsBuilder().append(value, other.value).append(collection, other.collection).isEquals();
    }
}
