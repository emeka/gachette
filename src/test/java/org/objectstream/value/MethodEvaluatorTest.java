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
import org.objectstream.instrumentation.ProxyFactory;

import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MethodEvaluatorTest {

    MethodEvaluator methodEvaluator;
    @Mock
    ProxyFactory proxyFactory;

    TestClass target;
    Method primitiveReadMethod, primitiveWriteMethod;

    @Before
    public void setup() throws NoSuchMethodException {
        target = new TestClass();
        primitiveReadMethod = TestClass.class.getMethod("getPrimitive", null);
        primitiveWriteMethod = TestClass.class.getMethod("setPrimitive", Integer.TYPE);
    }

    @Test
    public void test() throws NoSuchMethodException {
        methodEvaluator = new MethodEvaluator(target, primitiveWriteMethod, new Object[]{new Integer(10)}, proxyFactory);
        assertNull(methodEvaluator.eval());
        assertEquals(10, target.getPrimitive());

        methodEvaluator = new MethodEvaluator(target, primitiveReadMethod, null, proxyFactory);
        assertEquals(10, methodEvaluator.eval());

        verify(proxyFactory, times(2)).instrumentField(target);
    }

    @Test
    public void testToString() {
        methodEvaluator = new MethodEvaluator(target, primitiveWriteMethod, new Object[]{new Integer(10)}, proxyFactory);
        assertTrue(methodEvaluator.toString().startsWith("Method("));
    }

    @Test
    public void testSameValue() {
        MethodEvaluator methodEvaluator1 = new MethodEvaluator(target, primitiveWriteMethod, new Object[]{new Integer(10)}, proxyFactory);
        MethodEvaluator methodEvaluator2 = new MethodEvaluator(target, primitiveWriteMethod, new Object[]{new Integer(10)}, proxyFactory);

        assertEquals(methodEvaluator1.hashCode(), methodEvaluator2.hashCode());
        assertEquals(methodEvaluator1, methodEvaluator2);
    }

    @Test
    public void testDifferentValue() {
        TestClass otherTarget = new TestClass();

        MethodEvaluator methodEvaluator1 = new MethodEvaluator(target, primitiveWriteMethod, new Object[]{new Integer(10)}, proxyFactory);
        MethodEvaluator methodEvaluator2 = new MethodEvaluator(otherTarget, primitiveWriteMethod, new Object[]{new Integer(10)}, proxyFactory);
        MethodEvaluator methodEvaluator3 = new MethodEvaluator(target, primitiveWriteMethod, new Object[]{new Integer(20)}, proxyFactory);
        MethodEvaluator methodEvaluator4 = new MethodEvaluator(target, primitiveReadMethod, null, proxyFactory);

        assertNotEquals(methodEvaluator1.hashCode(), methodEvaluator2.hashCode());
        assertNotEquals(methodEvaluator1, methodEvaluator2);

        assertNotEquals(methodEvaluator1.hashCode(), methodEvaluator3.hashCode());
        assertNotEquals(methodEvaluator1, methodEvaluator3);

        assertNotEquals(methodEvaluator1.hashCode(), methodEvaluator4.hashCode());
        assertNotEquals(methodEvaluator1, methodEvaluator4);
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
