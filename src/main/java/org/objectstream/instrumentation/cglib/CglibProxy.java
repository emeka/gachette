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

package org.objectstream.instrumentation.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;
import org.objectstream.exceptions.ExceptionUtils;
import org.objectstream.instrumentation.MethodHandler;
import org.objectstream.instrumentation.ObjectStreamProxy;
import org.objectstream.instrumentation.ProxyProvider;

import java.lang.reflect.Method;

public class CglibProxy<T>  implements ProxyProvider<T> {

    MethodHandler methodHandler;

    public CglibProxy(MethodHandler methodHandler) {
        this.methodHandler = methodHandler;
    }

    public T create(T obj) {
        Enhancer e = new Enhancer();
        e.setSuperclass(obj.getClass());
        e.setCallback(new ListenerInterceptor(methodHandler));
        e.setInterfaces(new Class[]{ObjectStreamProxy.class});
        return (T) e.create();
    }

    private class ListenerInterceptor<T, M> implements net.sf.cglib.proxy.MethodInterceptor {
        private MethodHandler interceptor;

        public ListenerInterceptor(MethodHandler interceptor) {
            this.interceptor = interceptor;
        }

        public Object intercept(Object o, Method method, Object[] arguments, MethodProxy methodProxy) {
            Object res;
            try {
                res = interceptor.handle(o, method, arguments);
            } catch (Throwable e) {
                throw ExceptionUtils.wrap(e);
            }

            return res;
        }
    }
}

//http://in.relation.to/Bloggers/DeprecatedCGLIBSupport
//http://www.theserverside.com/news/1363571/Remote-Lazy-Loading-in-Hibernate
