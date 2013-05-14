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

package org.gachette.spi.callprocessor;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.gachette.instrumentation.GachetteProxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.gachette.context.CallContext;
import org.gachette.context.DefaultCallContext;
import org.gachette.instrumentation.ProxyFactory;
import org.gachette.spi.graphprovider.GraphProvider;
import org.gachette.value.Evaluator;
import org.gachette.value.Value;

import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCallProcessorTest {

    CallProcessor callProcessor;

    @Mock
    GraphProvider graphProvider;

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
        callProcessor = new DefaultCallProcessor(graphProvider,proxyFactory,context);

        primitiveReadMethod = TestClass.class.getMethod("getPrimitive", null);
        primitiveWriteMethod = TestClass.class.getMethod("setPrimitive", Integer.TYPE);
        objectReadMethod = TestClass.class.getMethod("getObject", null);
        objectWriteMethod = TestClass.class.getMethod("setObject", Object.class);
        voidMethod = TestClass.class.getMethod("modify", Integer.TYPE);
        nonVoidMethod = TestClass.class.getMethod("state", null);
        hashCodeMethod = TestClass.class.getMethod("hashCode", null);
        equalsMethod = TestClass.class.getMethod("equals", Object.class);
        getOriginalObjectMethod = TestClass.class.getMethod("getOriginalObject", null);

        when(graphProvider.value(any(Evaluator.class))).thenReturn(value);
        when(value.getValue()).thenReturn(oldValue);
        when(value.eval()).thenReturn(newValue);

        testObject = new TestClass();
    }

    @Test
    public void testHashCode(){
        GachetteProxy proxy = mock(GachetteProxy.class);
        when(proxy.getOriginalObject()).thenReturn(testObject);

        assertEquals(callProcessor.calculateHashCode(testObject), callProcessor.calculateHashCode(proxy));
    }

    @Test
    public void testGetPropertyNewValueEmptyValueStack() {
        assertTrue(context.empty());
        callProcessor.eval(object, primitiveReadMethod, parameters);

        assertTrue(context.empty());
        verify(graphProvider).notifyChange(value);
        verify(graphProvider, never()).bind(any(Value.class), any(Value.class));
    }

    @Test
    public void testGetPropertyOldValueEmptyValueStack() {
        when(value.eval()).thenReturn(oldValue);
        assertTrue(context.empty());
        callProcessor.eval(object, primitiveReadMethod, parameters);

        assertTrue(context.empty());
        verify(graphProvider, never()).notifyChange(any(Value.class));
        verify(graphProvider, never()).bind(any(Value.class), any(Value.class));
    }

    @Test
    public void testGetPropertyNewValueWithValueInStack() {
        context.push(parentValue);

        assertEquals(1, context.depth());
        callProcessor.eval(object, primitiveReadMethod, parameters);

        assertEquals(1, context.depth());
        assertEquals(parentValue, context.peek());

        verify(graphProvider).notifyChange(value);
        verify(graphProvider).bind(parentValue, value);
    }

    @Test
    public void testInvokeNonVoidMethod() {
        assertTrue(context.empty());
        callProcessor.eval(object, nonVoidMethod, parameters);

        assertTrue(context.empty());
        verify(graphProvider).notifyChange(value);
        verify(graphProvider, never()).bind(any(Value.class), any(Value.class));
    }

    @Test
    public void testSetPrimitiveProperty() {
        callProcessor.eval(testObject, primitiveWriteMethod, new Object[]{new Integer(33)});

        assertEquals(33, testObject.getPrimitive());
        verify(graphProvider).invalidate(any(Value.class));
    }

    @Test
    public void testSetObjectProperty() {
        Object object = new Object();
        callProcessor.eval(testObject, objectWriteMethod, new Object[]{object});

        assertEquals(object, testObject.getObject());
        verify(graphProvider, times(1)).invalidate(any(Value.class));
    }

    @Test
    public void testInvokeVoidMethod() {
        callProcessor.eval(testObject, voidMethod, new Object[]{new Integer(11)});

        assertEquals(11, testObject.state());
        verify(graphProvider, never()).invalidate(any(Value.class));
    }

    @Test
    public void testInvokeHashCode() {
        int hashCode = (Integer) callProcessor.eval(testObject, hashCodeMethod, new Object[]{});

        assertEquals(testObject.hashCode(), hashCode);
        verify(graphProvider, never()).value(any(Evaluator.class));
    }

    @Test
    public void testInvokeEquals() {
        assertTrue((Boolean) callProcessor.eval(testObject, equalsMethod, new Object[]{testObject}));
        verify(graphProvider, never()).value(any(Evaluator.class));
    }

    @Test
    public void testInvokeGetOriginalObject() {
        assertSame(testObject, callProcessor.eval(testObject, getOriginalObjectMethod, new Object[]{}));
        verify(graphProvider, never()).value(any(Evaluator.class));
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
