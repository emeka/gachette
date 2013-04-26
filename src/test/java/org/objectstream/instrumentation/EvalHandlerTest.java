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

package org.objectstream.instrumentation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.objectstream.context.CallContext;
import org.objectstream.spi.ObjectStreamProvider;
import org.objectstream.value.Evaluator;
import org.objectstream.value.Value;

import java.lang.reflect.Method;
import java.util.Stack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EvalHandlerTest {

    private EvalHandler handler;


    @Mock
    ObjectStreamProvider streamProvider;
    @Mock
    ProxyFactory proxyFactory;
    @Mock
    CallContext context;
    Stack<Value> stack;

    @Mock
    Object object;
    @Mock
    Value value, parentValue;
    @Mock
    Object oldValue, newValue;

    Method primitiveReadMethod, primitiveWriteMethod, objectReadMethod, objectWriteMethod, voidMethod, nonVoidMethod;
    Object[] parameters = new Object[]{};

    TestClass testObject;

    @Before
    public void setup() throws NoSuchMethodException {
        handler = new EvalHandler(streamProvider, proxyFactory, context);
        primitiveReadMethod = TestClass.class.getMethod("getPrimitive", null);
        primitiveWriteMethod = TestClass.class.getMethod("setPrimitive", Integer.TYPE);
        objectReadMethod = TestClass.class.getMethod("getObject", null);
        objectWriteMethod = TestClass.class.getMethod("setObject", Object.class);
        voidMethod = TestClass.class.getMethod("modify", Integer.TYPE);
        nonVoidMethod = TestClass.class.getMethod("state", null);

        stack = new Stack<>();

        when(context.getValueStack()).thenReturn(stack);
        when(streamProvider.value(any(Evaluator.class))).thenReturn(value);
        when(value.getValue()).thenReturn(oldValue);
        when(value.eval()).thenReturn(newValue);

        testObject = new TestClass();
    }

    @Test
    public void testGetPropertyNewValueEmptyValueStack() {
        assertTrue(stack.empty());
        handler.handle(object, primitiveReadMethod, parameters);

        assertTrue(stack.empty());
        verify(context).setLastValue(value);
        verify(streamProvider).notifyChange(value);
        verify(streamProvider, never()).bind(any(Value.class), any(Value.class));
    }

    @Test
    public void testGetPropertyOldValueEmptyValueStack() {
        when(value.eval()).thenReturn(oldValue);
        assertTrue(stack.empty());
        handler.handle(object, primitiveReadMethod, parameters);

        assertTrue(stack.empty());
        verify(context).setLastValue(value);
        verify(streamProvider, never()).notifyChange(any(Value.class));
        verify(streamProvider, never()).bind(any(Value.class), any(Value.class));
    }

    @Test
    public void testGetPropertyNewValueWithValueInStack() {
        stack.push(parentValue);

        assertEquals(1, stack.size());
        handler.handle(object, primitiveReadMethod, parameters);

        assertEquals(1, stack.size());
        assertEquals(parentValue, stack.peek());

        verify(context).setLastValue(value);
        verify(streamProvider).notifyChange(value);
        verify(streamProvider).bind(parentValue, value);
    }

    @Test
    public void testInvokeNonVoidMethod() {
        assertTrue(stack.empty());
        handler.handle(object, nonVoidMethod, parameters);

        assertTrue(stack.empty());
        verify(context).setLastValue(value);
        verify(streamProvider).notifyChange(value);
        verify(streamProvider, never()).bind(any(Value.class), any(Value.class));
    }

    @Test
    public void testSetPrimitiveProperty() {
        handler.handle(testObject, primitiveWriteMethod, new Object[]{new Integer(33)});

        assertEquals(33, testObject.getPrimitive());
        verify(streamProvider).invalidate(any(Value.class));
    }

    @Test
    public void testSetObjectProperty() {
        Object object = new Object();
        handler.handle(testObject, objectWriteMethod, new Object[]{object});

        assertEquals(object, testObject.getObject());
        verify(streamProvider, never()).invalidate(any(Value.class));
    }

    @Test
    public void testInvokeVoidMethod() {
        handler.handle(testObject, voidMethod, new Object[]{new Integer(11)});

        assertEquals(11, testObject.state());
        verify(streamProvider, never()).invalidate(any(Value.class));
    }

    private static class TestClass {
        private int primitive;
        private Object object;
        private int state;

        public void setPrimitive(int primitive) {
            this.primitive = primitive;
        }

        public int getPrimitive() {
            return primitive;
        }

        public void setObject(Object object) {
            this.object = object;
        }

        public Object getObject() {
            return object;
        }

        public void modify(int state) {
            this.state = state;
        }

        public int state() {
            return this.state;
        }
    }
}