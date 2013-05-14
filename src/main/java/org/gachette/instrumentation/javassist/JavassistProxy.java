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

package org.gachette.instrumentation.javassist;

import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.gachette.exceptions.ExceptionUtils;
import org.gachette.instrumentation.GachetteProxy;
import org.gachette.instrumentation.MethodHandler;
import org.gachette.instrumentation.ProxyProvider;

import java.lang.reflect.Method;

public class JavassistProxy<T> implements ProxyProvider<T> {

    MethodHandler methodHandler;

    public JavassistProxy(MethodHandler methodHandler) {
        this.methodHandler = methodHandler;
    }

    public T create(T obj) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(obj.getClass());
        proxyFactory.setInterfaces(new Class[]{GachetteProxy.class});
        Class<?> proxyClass = proxyFactory.createClass();
        Object wObjPK = null;
        try {
            wObjPK = proxyClass.newInstance();
        } catch (Throwable e) {
            throw ExceptionUtils.wrap(e);
        }
        ((ProxyObject) wObjPK).setHandler(new ListenerInterceptor(methodHandler));
        return (T) wObjPK;
    }

    private class ListenerInterceptor<T, M> implements javassist.util.proxy.MethodHandler {
        private MethodHandler interceptor;

        public ListenerInterceptor(MethodHandler interceptor) {
            this.interceptor = interceptor;
        }

        public Object invoke(Object o, Method method, Method proceed, Object[] arguments) {
            Object res = null;
            try {
                res = interceptor.handle(o, method, arguments);
            } catch (Throwable e) {
                throw ExceptionUtils.wrap(e);
            }
            return res;
        }
    }
}
