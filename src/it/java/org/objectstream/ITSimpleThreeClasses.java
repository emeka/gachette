package org.objectstream;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectstream.ObjectStream;
import org.objectstream.ObjectStreamManager;
import org.objectstream.value.ValueObserver;
import org.objectstream.model.A;
import org.objectstream.model.B;

import static junit.framework.Assert.assertEquals;

public class ITSimpleThreeClasses {

    private ObjectStream stream;

    @Before
    public void setup() {
        stream = ObjectStreamManager.create();
    }

    @After
    public void cleanup() {

    }

    @Test
    public void testSimple() {
        B b = new B();
        b.setValue(100);
        A a = new A();
        a.setValue(200);
        a.setB(b);

        assertEquals(300, a.getResult());
        assertEquals(200, stream.object(a).getValue());
        assertEquals(300, stream.object(a).getResult());

        stream.object(b).setValue(300);
        assertEquals(500, stream.object(a).getResult());
    }

    @Test
    public void testSimpleWithListener() {
        B b = new B();
        b.setValue(100);
        A a = new A();
        a.setValue(200);
        a.setB(b);

        assertEquals(300, a.getResult());
        assertEquals(300, stream.object(a).getResult());

        UpdateListener listener = new UpdateListener();

        stream.addListener(listener).to(a).getResult();
        stream.object(b).setValue(300);

        assertEquals(500, listener.getResult());
    }

    public class UpdateListener<T> implements ValueObserver<T> {
        private T result;

        private T getResult() {
            return result;
        }

        public void update(T result){
            this.result = result;
        }
    }
}
