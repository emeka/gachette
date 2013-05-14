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

package org.gachette.value;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ValueTest {

    @Mock
    Evaluator calculator1, calculator2;

    Value value;


    @Before
    public void setup() {
        when(calculator1.eval(anyObject(), anyBoolean())).thenReturn(100);
        when(calculator2.eval(anyObject(), anyBoolean())).thenReturn(200);
    }

    @Test
    public void testCreation() {
        value = new Value(calculator1);
        assertTrue(value.isDirty());
        assertNull(value.getValue());
        assertEquals(100, value.eval());
        assertEquals(100, value.getValue());
        assertFalse(value.isDirty());

        assertEquals(100, value.eval());
        value.setDirty(); //We force one more recalculation
        assertEquals(100, value.eval());

        verify(calculator1, times(2)).eval(anyObject(), eq(true)); //two calls to value.eval().
        verify(calculator1, times(1)).eval(anyObject(), eq(false));//one call with dirty=false
    }

    @Test
    public void testSameCalculators() {
        Value value1 = new Value(calculator1);
        Value value2 = new Value(calculator1);

        assertEquals(value1.hashCode(), value2.hashCode());
        assertEquals(value1, value2);

        assertTrue(value1.isDirty());
        assertTrue(value2.isDirty());

        value1.eval();
        assertFalse(value1.isDirty());
        assertNotEquals(value1.getValue(), value2.getValue());

        assertEquals(value1.hashCode(), value2.hashCode());
        assertEquals(value1, value2);
    }

    @Test
    public void testDifferentCalculators() {
        Value value1 = new Value(calculator1);
        Value value2 = new Value(calculator2);

        assertNotEquals(value1.hashCode(), value2.hashCode());
        assertNotEquals(value1, value2);

        assertTrue(value1.isDirty());
        assertTrue(value2.isDirty());

        value1.eval();
        assertFalse(value1.isDirty());
        assertNotEquals(value1.getValue(), value2.getValue());

        assertNotEquals(value1.hashCode(), value2.hashCode());
        assertNotEquals(value1, value2);
    }

    @Test
    public void testToString() {
        Value value1 = new Value(calculator1);
        assertTrue(value1.toString().startsWith("Value "));
    }
}
