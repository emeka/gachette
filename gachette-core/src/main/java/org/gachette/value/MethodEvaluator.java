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

package org.gachette.value;

import org.gachette.annotations.Volatile;
import org.gachette.exceptions.ExceptionUtils;
import org.gachette.spi.callprocessor.CallProcessor;
import org.gachette.utils.EqualsUtil;
import org.gachette.utils.HashCodeUtil;

import java.lang.reflect.Method;


public class MethodEvaluator<T> implements Evaluator<T> {

    private final Object object;
    private final Method method;
    private final Object[] parameters;
    private final CallProcessor callProcessor;
    private final boolean cachable;

    public MethodEvaluator(Object object, Method method, Object[] parameters, CallProcessor callProcessor) {
        if (object == null) {
            throw new RuntimeException("Object cannot be null;");
        }
        if (method == null) {
            throw new RuntimeException("Method cannot be null;");
        }

        this.object = object;
        this.method = method;
        this.parameters = parameters == null || parameters.length == 0 ? null : parameters;
        this.callProcessor = callProcessor;

        this.cachable = method.getAnnotation(Volatile.class) == null;
    }

    @Override
    public T eval(T current, boolean dirty) {

        if (cachable && !dirty) {
            return current;
        }

        callProcessor.enhance(object);

        T result;
        try {
            result = (T) method.invoke(object, parameters);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("Cannot invoke '%s' on %s. Please ensure that the method is public.", method, object), e);
        } catch (Throwable e) {
            throw ExceptionUtils.wrap(e);
        }

        return result;
    }

    @Override
    public Object targetObject() {
        return object;
    }

    @Override
    public int hashCode() {
        //We use ProxyUtils.calculateHashCode because the object hash code should not change if the object field values change.
        //A MethodEvaluator should return the same hash code if calling the same method on the same object with the
        //same parameters.  This will cause problem when we are using a distributed solution spanning several VM as we
        //want to be able to send value update cross VM.  We will certainly need an id as Hibernate does.

        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, HashCodeUtil.identityHash(object));
        result = HashCodeUtil.hash(result, method);
        result = HashCodeUtil.hash(result, parameters);

        return result;
    }

    @Override
    public boolean equals(Object otherObject) {
        if(!EqualsUtil.isSameClass(this, otherObject)){
            return false;
        }
        MethodEvaluator other = (MethodEvaluator) otherObject;

        boolean result = EqualsUtil.same(true, object, other.object);
        result = EqualsUtil.equals(result,method, other.method);
        result = EqualsUtil.equals(result,parameters, other.parameters);

        return result;
    }

    public String toString() {
        return String.format("Method(%s,%s,%s)", object, method, parameters);
    }
}
