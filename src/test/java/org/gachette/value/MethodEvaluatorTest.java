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

package org.gachette.value;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.gachette.spi.callprocessor.CallProcessor;

import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MethodEvaluatorTest {

    MethodEvaluator methodEvaluator;

    @Mock
    CallProcessor callProcessor;

    TestClass target;
    Method primitiveReadMethod, primitiveWriteMethod;

    @Before
    public void setup() throws NoSuchMethodException {
        target = new TestClass();
        primitiveReadMethod = TestClass.class.getMethod("getPrimitive", null);
        primitiveWriteMethod = TestClass.class.getMethod("setPrimitive", Integer.TYPE);
        when(callProcessor.calculateHashCode(any(Object.class))).thenReturn(1111);
    }

    @Test
    public void test() throws NoSuchMethodException {
        methodEvaluator = new MethodEvaluator(target, primitiveWriteMethod, new Object[]{new Integer(10)}, callProcessor);
        assertNull(methodEvaluator.eval(null, true));
        assertEquals(10, target.getPrimitive());

        methodEvaluator = new MethodEvaluator(target, primitiveReadMethod, null, callProcessor);
        assertEquals(10, methodEvaluator.eval(null, true));

        verify(callProcessor, times(2)).enhance(target);
    }

    @Test
    public void testEqualValue() {
        MethodEvaluator methodEvaluator1 = new MethodEvaluator(target, primitiveWriteMethod, new Object[]{new Integer(10)}, callProcessor);
        MethodEvaluator methodEvaluator2 = new MethodEvaluator(target, primitiveWriteMethod, new Object[]{new Integer(10)}, callProcessor);

        assertEquals(methodEvaluator1.hashCode(), methodEvaluator2.hashCode());
        assertEquals(methodEvaluator1, methodEvaluator2);
    }

    @Test
    public void testNotEqualObject() {
        TestClass otherTarget = new TestClass();

        MethodEvaluator methodEvaluator1 = new MethodEvaluator(target, primitiveWriteMethod, new Object[]{new Integer(10)}, callProcessor);
        MethodEvaluator methodEvaluator2 = new MethodEvaluator(otherTarget, primitiveWriteMethod, new Object[]{new Integer(10)}, callProcessor);
        when(callProcessor.calculateHashCode(target)).thenReturn(1111);
        when(callProcessor.calculateHashCode(otherTarget)).thenReturn(2222);

        assertNotEquals(methodEvaluator1.hashCode(), methodEvaluator2.hashCode());
        assertNotEquals(methodEvaluator1, methodEvaluator2);
    }

    @Test
    public void testNotEqualMethod() {
        MethodEvaluator methodEvaluator1 = new MethodEvaluator(target, primitiveWriteMethod, new Object[]{new Integer(10)}, callProcessor);
        MethodEvaluator methodEvaluator3 = new MethodEvaluator(target, primitiveWriteMethod, new Object[]{new Integer(20)}, callProcessor);

        assertNotEquals(methodEvaluator1.hashCode(), methodEvaluator3.hashCode());
        assertNotEquals(methodEvaluator1, methodEvaluator3);
    }

    @Test
    public void testNotEqualParameters() {
        MethodEvaluator methodEvaluator1 = new MethodEvaluator(target, primitiveWriteMethod, new Object[]{new Integer(10)}, callProcessor);
        MethodEvaluator methodEvaluator4 = new MethodEvaluator(target, primitiveReadMethod, null, callProcessor);

        assertNotEquals(methodEvaluator1.hashCode(), methodEvaluator4.hashCode());
        assertNotEquals(methodEvaluator1, methodEvaluator4);
    }

    @Test
    public void testToString() {
        methodEvaluator = new MethodEvaluator(target, primitiveWriteMethod, new Object[]{new Integer(10)}, callProcessor);
        assertTrue(methodEvaluator.toString().startsWith("Method("));
    }

    private static class TestClass {
        private int primitive;

        public void setPrimitive(int primitive) {
            this.primitive = primitive;
        }

        public int getPrimitive() {
            return primitive;
        }
    }
}
