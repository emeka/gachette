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

package org.gachette.spi.graphprovider.collection;

import org.gachette.model.A;
import org.gachette.model.B;
import org.gachette.spi.callprocessor.CallProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.gachette.context.CallContext;
import org.gachette.instrumentation.ProxyFactory;
import org.gachette.spi.graphprovider.collection.CollectionGraphProvider;
import org.gachette.value.Value;
import org.gachette.value.ValueObserver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CollectionGraphProviderTest {

    private CollectionGraphProvider streamProvider;

    @Mock
    ProxyFactory proxyFactory;

    @Mock
    CallContext context;

    Object object1, object2;

    Method method1, method2;

    Object[] parameters1, parameters2;

    @Mock
    CallProcessor callProcessor;

    @Mock
    ValueObserver observer;

    @Mock
    Value unknownValue;

    @Before
    public void setup() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        streamProvider = new CollectionGraphProvider();
        method1 = A.class.getMethod("getValue",null);
        method2 = B.class.getMethod("getValue",null);
        object1 = new A();
        object2 = new B();
    }

    @Test
    public void testValue() {
        Value value1 = streamProvider.value(object1, method1, parameters1, callProcessor);

        //calling twice with the same evaluator should return the same value
        assertEquals(value1, streamProvider.value(object1, method1, parameters1, callProcessor));

        Value value2 = streamProvider.value(object2, method2, parameters2, callProcessor);
        assertNotSame(value1, value2);
    }

    @Test
    public void testObserveAndNotify() {
        Value value1 = streamProvider.value(object1, method1, parameters1,callProcessor);

        streamProvider.observe(value1, observer); //should notify value1
        streamProvider.observe(value1, observer); //should not notify value1 since observer already exists

        streamProvider.notifyChange(value1); //should notify value1
        streamProvider.notifyChange(value1); //should notify value1
        verify(observer, times(3)).notify(value1);
    }

    @Test
    public void testBindAndUnbind() {
        Value value1 = streamProvider.value(object1, method1, parameters1,callProcessor);
        Value value2 = streamProvider.value(object2, method2, parameters2,callProcessor);
        streamProvider.bind(value1, value2);

        streamProvider.observe(value1, observer); //should notify value1

        streamProvider.notifyChange(value2);  //should not notify value1 as notification does not follow binds


        streamProvider.unbind(value1, value2);
        streamProvider.invalidate(value2); //should not notify value1 after the unbind

        verify(observer, times(1)).notify(value1);
    }

    @Test(expected = RuntimeException.class)
    public void testBindUnknownValue() {
        Value value1 = streamProvider.value(object1, method1, parameters1,callProcessor);
        streamProvider.bind(value1, unknownValue);
    }

    @Test
    public void testInvalidation() {
        Value value1 = streamProvider.value(object1, method1, parameters1,callProcessor);
        Value value2 = streamProvider.value(object2, method2, parameters2,callProcessor);

        streamProvider.bind(value1, value2);
        streamProvider.observe(value1, observer); //should notify value1

        value1.eval();
        value2.eval();

        assertFalse(value1.isDirty());
        assertFalse(value2.isDirty());

        streamProvider.invalidate(value2);  //should notify as invalidation follow binds and value changes trigger observers
        verify(observer, times(2)).notify(value1);

        assertTrue(value1.isDirty());
        assertTrue(value2.isDirty());
    }
}
