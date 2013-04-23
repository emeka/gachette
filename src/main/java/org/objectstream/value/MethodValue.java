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

import org.objectstream.instrumentation.ProxyFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;


public class MethodValue<T> implements ValueCalculator<T> {

    private final Object object;
    private final Method method;
    private final Object[] parameters;
    private final ProxyFactory proxyFactory;

    public MethodValue(Object object, Method method, Object[] parameters, ProxyFactory proxyFactory) {
        this.object = object;
        this.method = method;
        this.parameters = parameters;
        this.proxyFactory = proxyFactory;
    }

    @Override
    public T calculate(Map<Value, Object> dependencies) {
        proxyFactory.instrumentField(object);

        T result;
        try {
            result = (T) method.invoke(object, parameters);
        } catch (RuntimeException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("Cannot invoke '%s' on %s. Please ensure that the method is public.", method, object), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
}
