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

package org.objectstream.instrumentation;


import org.objectstream.value.MethodValue;
import org.objectstream.value.Value;
import org.objectstream.ObjectStream;
import org.objectstream.value.ValueObserver;

import java.lang.reflect.Method;

public class ListenerInterceptor<T,M> implements MethodInterceptor {
    private ObjectStream stream;
    private T realObj;
    private ValueObserver<M> listener;
    private final ProxyFactory proxyFactory;

    public ListenerInterceptor(T realObj, ObjectStream stream, ValueObserver<M> listener, ProxyFactory proxyFactory) {
        this.realObj = realObj;
        this.stream = stream;
        this.listener = listener;
        this.proxyFactory = proxyFactory;
    }

    public Object intercept(Object o, Method method, Object[] objects) {
        stream.observe(new Value(new MethodValue(realObj, method, objects, proxyFactory)), listener);
        return null;
    }
}
