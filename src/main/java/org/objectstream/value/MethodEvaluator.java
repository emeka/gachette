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

package org.objectstream.value;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.objectstream.exceptions.ExceptionUtils;
import org.objectstream.spi.ObjectStreamProvider;

import java.lang.reflect.Method;


public class MethodEvaluator<T> implements Evaluator<T> {

    private final Object object;
    private final Method method;
    private final Object[] parameters;
    private final ObjectStreamProvider objectStreamProvider;

    public MethodEvaluator(Object object, Method method, Object[] parameters, ObjectStreamProvider objectStreamProvider) {
        this.object = object;
        this.method = method;
        this.parameters = parameters;
        this.objectStreamProvider = objectStreamProvider;
    }

    @Override
    public T eval() {
        objectStreamProvider.enhance(object);

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
    public int hashCode() {
        //We use ProxyUtils.hashCode because the object hash code should not change if the object field values change.
        //A MethodEvaluator should return the same hash code if calling the same method on the same object with the
        //same parameters.  This will cause problem when we are using a distributed solution spanning several VM as we
        //want to be able to send value update cross VM.  We will certainly need an id as Hibernate does.
        return new HashCodeBuilder().append(objectStreamProvider.hashCode(object)).append(method).append(parameters).toHashCode();
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == this) return true;
        if (otherObject == null) return false;
        if (this.getClass() != otherObject.getClass()) return false;
        MethodEvaluator other = (MethodEvaluator) otherObject;

        return new EqualsBuilder()
                .append(objectStreamProvider.hashCode(object),objectStreamProvider.hashCode(other.object))
                .append(method, other.method)
                .append(parameters, other.parameters).isEquals();
    }

    public String toString() {
        return String.format("Method(%s,%s,%s)", object, method, parameters);
    }
}
