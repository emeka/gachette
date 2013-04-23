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
import net.sf.cglib.transform.impl.InterceptFieldEnabled;
import org.objectstream.instrumentation.MethodInterceptor;
import org.objectstream.instrumentation.ObjectStreamProxy;
import org.objectstream.instrumentation.ProxyFactory;
import org.objectstream.instrumentation.ProxyProvider;

import java.lang.reflect.Method;

public class CglibProxy<T>  implements ProxyProvider<T> {

    MethodInterceptor interceptor;

    public CglibProxy(MethodInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    public T create(T obj) {
        Enhancer e = new Enhancer();
        e.setSuperclass(obj.getClass());
        e.setCallback(new ListenerInterceptor(interceptor));
        e.setInterfaces(new Class[]{ObjectStreamProxy.class});
        return (T) e.create();
    }

    private class ListenerInterceptor<T, M> implements net.sf.cglib.proxy.MethodInterceptor {
        private MethodInterceptor interceptor;

        public ListenerInterceptor(MethodInterceptor interceptor) {
            this.interceptor = interceptor;
        }

        public Object intercept(Object o, Method method, Object[] arguments, MethodProxy methodProxy) {
            Object res;
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

//http://in.relation.to/Bloggers/DeprecatedCGLIBSupport
//http://www.theserverside.com/news/1363571/Remote-Lazy-Loading-in-Hibernate
