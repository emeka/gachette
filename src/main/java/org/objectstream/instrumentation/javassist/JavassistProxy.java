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

package org.objectstream.instrumentation.javassist;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.objectstream.instrumentation.MethodInterceptor;
import org.objectstream.instrumentation.ObjectStreamProxy;
import org.objectstream.instrumentation.ProxyProvider;

import java.lang.reflect.Method;

public class JavassistProxy<T> implements ProxyProvider<T> {

    MethodInterceptor interceptor;

    public JavassistProxy(MethodInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    public T create(T obj) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(obj.getClass());
        proxyFactory.setInterfaces(new Class[]{ObjectStreamProxy.class});
        Class<?> proxyClass = proxyFactory.createClass();
        Object wObjPK = null;
        try {
            wObjPK = proxyClass.newInstance();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ((ProxyObject) wObjPK).setHandler(new ListenerInterceptor(interceptor));
        return (T) wObjPK;
    }

    private class ListenerInterceptor<T, M> implements MethodHandler {
        private MethodInterceptor interceptor;

        public ListenerInterceptor(MethodInterceptor interceptor) {
            this.interceptor = interceptor;
        }

        public Object invoke(Object o, Method method, Method proceed, Object[] arguments) {
            Object res = null;
            try {
                res = interceptor.intercept(o, method, arguments);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return res;
        }
    }
}
