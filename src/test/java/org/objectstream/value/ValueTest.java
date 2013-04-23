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

import java.util.Map;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValueTest {

    @Mock
    ValueCalculator calculator;

    Value value;


    @Before
    public void setup() {
        when(calculator.calculate(any(Map.class))).thenReturn(100);
    }

    @Test
    public void testCreation() {
        value = new Value(calculator);
        assertTrue(value.isDirty());
        assertEquals(100, value.getValue());
    }

    @Test
    public void testDependencies() {

        value = new Value(new TestCalculator());
        assertTrue(value.isDirty());

        value.update(new Value(new ConstantValue(100), true));
        assertEquals(100, value.getValue());
        assertFalse(value.isDirty());

        value.update(new Value(new ConstantValue(200), true));
        assertTrue(value.isDirty());
        assertEquals(300, value.getValue());
        assertFalse(value.isDirty());
    }

    private class TestCalculator implements ValueCalculator<Integer> {

        @Override
        public Integer calculate(Map<Value, Object> dependencies) {
            int result = 0;
            for (Object value : dependencies.values()) {
                if (value instanceof Integer) {
                    result += (Integer) value;
                }
            }
            return result;
        }
    }
}