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

package org.gachette.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.gachette.context.CallContext;
import org.gachette.spi.callprocessor.CallProcessor;
import org.gachette.spi.graphprovider.GraphProvider;
import org.gachette.value.Value;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FluentObserveValueTest {
    @Mock
    CallProcessor callProcessor;

    @Mock
    GraphProvider graphProvider;

    @Mock
    CallContext callContext;

    @Mock
    Value value;

    @Test
    public void testValue(){
        when(callProcessor.getContext()).thenReturn(callContext);
        when(callContext.getLastValue()).thenReturn(value);
        assertTrue((new FluentObserveValue(callProcessor, graphProvider)).value(new Object()) instanceof FluentObserveWith);
    }

    @Test(expected = RuntimeException.class)
    public void testException(){
        when(callContext.getLastValue()).thenReturn(null);
        assertTrue((new FluentObserveValue(callProcessor, graphProvider)).value(new Object()) instanceof FluentObserveWith);
    }
}
