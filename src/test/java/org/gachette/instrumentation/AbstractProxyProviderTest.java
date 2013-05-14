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

package org.gachette.instrumentation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.lang.reflect.Method;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public abstract class AbstractProxyProviderTest {
    private static final Integer PROXY_VALUE = new Integer(10);

    private ProxyProvider<A> proxy;

    @Mock
    MethodHandler interceptor;

    @Before
    public void setup(){
        proxy = getProxyFactory(interceptor);
        when(interceptor.handle(any(Object.class), any(Method.class), any(Object[].class))).thenReturn(PROXY_VALUE);
    }

    @Test
    public void test(){
        A a = new A();
        a.setValue(20);

        A proxiedValue = proxy.create(a);

        assertEquals(20, a.intValue());
        assertEquals(PROXY_VALUE.intValue(), proxiedValue.intValue());
        assertTrue(proxiedValue instanceof GachetteProxy);
    }

    protected abstract <T> ProxyProvider<T> getProxyFactory(MethodHandler interceptor);
}
