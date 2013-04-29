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

import org.objectstream.exceptions.ExceptionUtils;
import org.objectstream.instrumentation.ProxyFactory;

import java.lang.reflect.Method;
import java.util.Arrays;


public class MethodEvaluator<T> implements Evaluator<T> {

    private final Object object;
    private final Method method;
    private final Object[] parameters;
    private final ProxyFactory proxyFactory;

    public MethodEvaluator(Object object, Method method, Object[] parameters, ProxyFactory proxyFactory) {
        this.object = object;
        this.method = method;
        this.parameters = parameters;
        this.proxyFactory = proxyFactory;
    }

    @Override
    public T eval() {
        proxyFactory.instrumentField(object);

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

    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + object.hashCode();
        hash = hash * 31 + method.hashCode();
        hash = hash * 13 + Arrays.hashCode(parameters);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) return true;
        if (object == null) return false;
        if (this.getClass() != object.getClass()) return false;
        MethodEvaluator other = (MethodEvaluator) object;

        return this.object.equals(other.object) &&
                this.method.equals(other.method) &&
                Arrays.equals(this.parameters, other.parameters);
    }

    public String toString() {
        return String.format("Method(%s,%s,%s)", object, method, parameters);
    }
}
