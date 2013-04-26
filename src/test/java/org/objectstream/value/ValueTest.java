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

package org.objectstream.value;


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
        when(calculator1.eval()).thenReturn(100);
        when(calculator2.eval()).thenReturn(200);
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

        verify(calculator1, times(2)).eval(); //three calls to value.eval() but only two call to the calculator.eval().
    }

    @Test
    public void testCreationWithEval() {
        value = new Value(calculator1, true);
        assertFalse(value.isDirty());
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
