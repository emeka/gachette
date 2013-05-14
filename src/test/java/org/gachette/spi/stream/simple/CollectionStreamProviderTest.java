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

package org.gachette.spi.stream.simple;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.gachette.context.CallContext;
import org.gachette.instrumentation.ProxyFactory;
import org.gachette.spi.graphprovider.collection.CollectionGraphProvider;
import org.gachette.value.Evaluator;
import org.gachette.value.Value;
import org.gachette.value.ValueObserver;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CollectionStreamProviderTest {

    private CollectionGraphProvider streamProvider;

    @Mock
    ProxyFactory proxyFactory;

    @Mock
    CallContext context;

    @Mock
    Evaluator evaluator1, evaluator2;

    @Mock
    ValueObserver observer;

    @Mock
    Value unknownValue;

    @Before
    public void setup() {
        streamProvider = new CollectionGraphProvider();
        when(evaluator1.eval(anyObject(), anyBoolean())).thenReturn(null);
        when(evaluator2.eval(anyObject(), anyBoolean())).thenReturn(null);
    }

    @Test
    public void testValue() {
        Value value1 = streamProvider.value(evaluator1);

        //calling twice with the same evaluator should return the same value
        assertEquals(value1, streamProvider.value(evaluator1));

        Value value2 = streamProvider.value(evaluator2);
        assertNotSame(value1, value2);
    }

    @Test
    public void testObserveAndNotify() {
        Value value1 = streamProvider.value(evaluator1);

        streamProvider.observe(value1, observer); //should notify value1
        streamProvider.observe(value1, observer); //should not notify value1 since observer already exists

        streamProvider.notifyChange(value1); //should notify value1
        streamProvider.notifyChange(value1); //should notify value1
        verify(observer, times(3)).notify(value1);
    }

    @Test
    public void testBindAndUnbind() {
        Value value1 = streamProvider.value(evaluator1);
        Value value2 = streamProvider.value(evaluator2);
        streamProvider.bind(value1, value2);

        streamProvider.observe(value1, observer); //should notify value1

        streamProvider.notifyChange(value2);  //should not notify value1 as notification does not follow binds


        streamProvider.unbind(value1, value2);
        streamProvider.invalidate(value2); //should not notify value1 after the unbind

        verify(observer, times(1)).notify(value1);
    }

    @Test(expected = RuntimeException.class)
    public void testBindUnknownValue(){
        Value value1 = streamProvider.value(evaluator1);
        streamProvider.bind(value1, unknownValue);
    }

    @Test
    public void testInvalidation() {
        Value value1 = streamProvider.value(evaluator1);
        Value value2 = streamProvider.value(evaluator2);

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
