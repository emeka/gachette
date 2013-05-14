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
