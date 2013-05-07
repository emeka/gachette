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

package org.objectstream.spi;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.objectstream.context.CallContext;
import org.objectstream.context.DefaultCallContext;
import org.objectstream.instrumentation.ObjectStreamProxy;
import org.objectstream.instrumentation.ProxyFactory;
import org.objectstream.value.Evaluator;
import org.objectstream.value.Value;

import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultObjectStreamProviderTest {

    ObjectStreamProvider objectStreamProvider;

    @Mock
    StreamBuilder streamBuilder;

    @Mock
    ProxyFactory proxyFactory;

    CallContext context;

    @Mock
    Object object;
    @Mock
    Value value, parentValue;
    @Mock
    Object oldValue, newValue;

    Method primitiveReadMethod, primitiveWriteMethod, objectReadMethod, objectWriteMethod, voidMethod,
            nonVoidMethod, hashCodeMethod, equalsMethod, getOriginalObjectMethod;

    Object[] parameters = new Object[]{};
    
    TestClass testObject;

    @Before
    public void setup() throws NoSuchMethodException {
        context = new DefaultCallContext(){};
        objectStreamProvider = new DefaultObjectStreamProvider(streamBuilder, proxyFactory, context);

        primitiveReadMethod = TestClass.class.getMethod("getPrimitive", null);
        primitiveWriteMethod = TestClass.class.getMethod("setPrimitive", Integer.TYPE);
        objectReadMethod = TestClass.class.getMethod("getObject", null);
        objectWriteMethod = TestClass.class.getMethod("setObject", Object.class);
        voidMethod = TestClass.class.getMethod("modify", Integer.TYPE);
        nonVoidMethod = TestClass.class.getMethod("state", null);
        hashCodeMethod = TestClass.class.getMethod("hashCode", null);
        equalsMethod = TestClass.class.getMethod("equals", Object.class);
        getOriginalObjectMethod = TestClass.class.getMethod("getOriginalObject", null);

        when(streamBuilder.value(any(Evaluator.class))).thenReturn(value);
        when(value.getValue()).thenReturn(oldValue);
        when(value.eval()).thenReturn(newValue);

        testObject = new TestClass();
    }

    @Test
    public void testHashCode(){
        ObjectStreamProxy proxy = mock(ObjectStreamProxy.class);
        when(proxy.getOriginalObject()).thenReturn(testObject);

        assertEquals(objectStreamProvider.calculateHashCode(testObject), objectStreamProvider.calculateHashCode(proxy));
    }

    @Test
    public void testGetPropertyNewValueEmptyValueStack() {
        assertTrue(context.empty());
        objectStreamProvider.eval(object, primitiveReadMethod, parameters);

        assertTrue(context.empty());
        verify(streamBuilder).notifyChange(value);
        verify(streamBuilder, never()).bind(any(Value.class), any(Value.class));
    }

    @Test
    public void testGetPropertyOldValueEmptyValueStack() {
        when(value.eval()).thenReturn(oldValue);
        assertTrue(context.empty());
        objectStreamProvider.eval(object, primitiveReadMethod, parameters);

        assertTrue(context.empty());
        verify(streamBuilder, never()).notifyChange(any(Value.class));
        verify(streamBuilder, never()).bind(any(Value.class), any(Value.class));
    }

    @Test
    public void testGetPropertyNewValueWithValueInStack() {
        context.push(parentValue);

        assertEquals(1, context.depth());
        objectStreamProvider.eval(object, primitiveReadMethod, parameters);

        assertEquals(1, context.depth());
        assertEquals(parentValue, context.peek());

        verify(streamBuilder).notifyChange(value);
        verify(streamBuilder).bind(parentValue, value);
    }

    @Test
    public void testInvokeNonVoidMethod() {
        assertTrue(context.empty());
        objectStreamProvider.eval(object, nonVoidMethod, parameters);

        assertTrue(context.empty());
        verify(streamBuilder).notifyChange(value);
        verify(streamBuilder, never()).bind(any(Value.class), any(Value.class));
    }

    @Test
    public void testSetPrimitiveProperty() {
        objectStreamProvider.eval(testObject, primitiveWriteMethod, new Object[]{new Integer(33)});

        assertEquals(33, testObject.getPrimitive());
        verify(streamBuilder).invalidate(any(Value.class));
    }

    @Test
    public void testSetObjectProperty() {
        Object object = new Object();
        objectStreamProvider.eval(testObject, objectWriteMethod, new Object[]{object});

        assertEquals(object, testObject.getObject());
        verify(streamBuilder, times(1)).invalidate(any(Value.class));
    }

    @Test
    public void testInvokeVoidMethod() {
        objectStreamProvider.eval(testObject, voidMethod, new Object[]{new Integer(11)});

        assertEquals(11, testObject.state());
        verify(streamBuilder, never()).invalidate(any(Value.class));
    }

    @Test
    public void testInvokeHashCode() {
        int hashCode = (Integer) objectStreamProvider.eval(testObject, hashCodeMethod, new Object[]{});

        assertEquals(testObject.hashCode(), hashCode);
        verify(streamBuilder, never()).value(any(Evaluator.class));
    }

    @Test
    public void testInvokeEquals() {
        assertTrue((Boolean) objectStreamProvider.eval(testObject, equalsMethod, new Object[]{testObject}));
        verify(streamBuilder, never()).value(any(Evaluator.class));
    }

    @Test
    public void testInvokeGetOriginalObject() {
        assertSame(testObject, objectStreamProvider.eval(testObject, getOriginalObjectMethod, new Object[]{}));
        verify(streamBuilder, never()).value(any(Evaluator.class));
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

        //Just to get the corresponding Method object
        public Object getOriginalObject() {
            return null;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(primitive).append(object).append(state).toHashCode();
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) return true;
            if (object == null) return false;
            if (this.getClass() != object.getClass()) return false;
            TestClass other = (TestClass) object;
            return new EqualsBuilder().append(primitive, other.primitive)
                    .append(object, other.object).append(state, other.state).isEquals();
        }
    }
}
