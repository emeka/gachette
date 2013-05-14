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

package org.gachette;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.gachette.api.FluentObserveValue;
import org.gachette.spi.callprocessor.CallProcessor;
import org.gachette.spi.graphprovider.GraphProvider;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultGachetteTest {

    DefaultGachette gachette;

    @Mock
    CallProcessor callProcessor;

    @Mock
    GraphProvider graphProvider;

    @Before
    public void setup(){
        gachette = new DefaultGachette(callProcessor, graphProvider);
    }

    @Test
    public void test(){
        assertTrue(gachette.observe() instanceof FluentObserveValue);
        Object target = new Object();
        gachette.object(target);
        verify(callProcessor).createProxy(target);
    }

}
