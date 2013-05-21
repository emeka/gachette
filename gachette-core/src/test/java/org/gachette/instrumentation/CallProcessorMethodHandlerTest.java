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

import org.gachette.spi.CallProcessorMethodHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.gachette.spi.callprocessor.CallProcessor;

import java.lang.reflect.Method;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CallProcessorMethodHandlerTest {

    private org.gachette.spi.CallProcessorMethodHandler handler;

    @Mock
    CallProcessor callProcessor;

    @Mock
    Object object, realObject;
    Object[] parameters;
    Method method;

    @Before
    public void setup() throws NoSuchMethodException {
        handler = new org.gachette.spi.CallProcessorMethodHandler(realObject, callProcessor);
        parameters = new Object[]{};
        method = TestClass.class.getMethod("getValue", null);
    }

    @Test
    public void testGetPropertyNewValueEmptyValueStack() {
        handler.handle(object, method, parameters);

        verify(callProcessor).eval(realObject, method, parameters);
    }

    private static class TestClass {
        public int getValue() {
            return 0;
        }
    }
}