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


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.gachette.model.A;
import org.gachette.model.B;
import org.gachette.model.C;
import org.gachette.value.Value;
import org.gachette.value.ValueObserver;

import static org.junit.Assert.assertEquals;

public class ITSimpleThreeClasses {

    private GachetteFactory manager;
    private Gachette stream;
    private A a;
    private B b;
    private C c;

    @Before
    public void setup() {
        manager = new DefaultGachetteFactory();
        stream = manager.create();

        a = stream.object(new A());
        b = stream.object(new B());
        c = stream.object(new C());
    }

    @After
    public void cleanup() {

    }

    @Test
    public void testSimpleSetValueBeforeProxying() {
        c = new C();
        b = new B();
        a = new A();

        c.setValue(1);
        b.setValue(10);
        b.setC(c);
        a.setValue(100);
        a.setB(b);

        assertEquals(100, a.getValue());
        assertEquals(111, a.getResult());
        assertEquals(100, stream.object(a).getValue());
        assertEquals(111, stream.object(a).getResult());
        assertEquals(10, stream.object(b).getValue());
        assertEquals(11, stream.object(b).getResult());
        assertEquals(1, stream.object(c).getValue());

        stream.object(c).setValue(2);

        assertEquals(100, stream.object(a).getValue());
        assertEquals(112, stream.object(a).getResult());
        assertEquals(10, stream.object(b).getValue());
        assertEquals(12, stream.object(b).getResult());
        assertEquals(2, stream.object(c).getValue());
    }

    @Test
    public void testSimpleSetValueOnProxy() {
        c.setValue(1);
        b.setValue(10);
        b.setC(c);
        a.setValue(100);
        a.setB(b);

        assertEquals(111, a.getResult());
        assertEquals(100, a.getValue());
        assertEquals(10, b.getValue());
        assertEquals(11, b.getResult());
        assertEquals(1, c.getValue());

        c.setValue(2);

        assertEquals(100, a.getValue());
        assertEquals(112, a.getResult());
        assertEquals(10, b.getValue());
        assertEquals(12, b.getResult());
        assertEquals(2, c.getValue());
    }

    @Test
    public void testSetNewObject(){
        c.setValue(1);
        b.setValue(10);
        b.setC(c);
        a.setValue(100);
        a.setB(b);

        assertEquals(111, a.getResult());

        b = stream.object(new B());
        b.setValue(20);
        b.setC(c);
        a.setB(b);

        assertEquals(121, a.getResult());
    }

    @Test
    public void testSetObjectToNull(){
        c.setValue(1);
        b.setValue(10);
        b.setC(c);
        a.setValue(100);
        a.setB(b);

        assertEquals(111, a.getResult());

        a.setB(null);

        assertEquals(100, a.getResult());
    }

    @Test
    public void testSimpleWithListener() {
        c.setValue(1);
        b.setValue(10);
        b.setC(c);
        a.setValue(100);
        a.setB(b);

        TestObserver<Long> listener = new TestObserver<>();

        a.getResult();
        stream.observe().value(a.getResult()).with(listener);
        assertEquals(111, listener.getResult().longValue());
        c.setValue(2);
        assertEquals(112, listener.getResult().longValue());
    }

    public class TestObserver<T> implements ValueObserver<T> {
        private T result;

        private T getResult() {
            return result;
        }

        public void notify(Value<T> value) {
            this.result = value.eval();
        }
    }
}
