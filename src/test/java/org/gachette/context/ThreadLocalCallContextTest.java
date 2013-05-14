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

package org.gachette.context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.gachette.instrumentation.MethodHandler;
import org.gachette.value.Value;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ThreadLocalCallContextTest {

    private ThreadLocalCallContext context;

    @Mock
    Value value;

    @Mock
    MethodHandler methodHandler;

    @Before
    public void setup() {
        context = new ThreadLocalCallContext(){};
        context.reset(); //We need to reset as creating a new object ThreadLocal object does not create a new context
    }

    @Test
    public void test() {
        assertNull(context.getLastValue());

        assertTrue(context.empty());

        context.push(value);

        assertNotNull(context.getLastValue());
        assertFalse(context.empty());

        context.reset();

        assertNull(context.getLastValue());
        assertTrue(context.empty());
    }
}
