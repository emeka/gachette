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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.objectstream.instrumentation.cglib.CglibProxy;

import java.lang.reflect.Method;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public abstract class AbstractProxyProviderTest {
    private static final Integer PROXY_VALUE = new Integer(10);

    private ProxyProvider<A> proxy;

    @Mock
    MethodInterceptor interceptor;

    @Before
    public void setup(){
        proxy = getProxyFactory(interceptor);
        when(interceptor.intercept(any(Object.class), any(Method.class), any(Object[].class))).thenReturn(PROXY_VALUE);
    }

    @Test
    public void test(){
        A a = new A();
        a.setValue(20);

        A proxiedValue = proxy.create(a);

        assertEquals(20, a.intValue());
        assertEquals(PROXY_VALUE.intValue(), proxiedValue.intValue());
        assertTrue(proxiedValue instanceof ObjectStreamProxy);
    }

    protected abstract <T> ProxyProvider<T> getProxyFactory(MethodInterceptor interceptor);
}
