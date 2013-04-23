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

package org.objectstream;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectstream.model.C;
import org.objectstream.value.ValueObserver;
import org.objectstream.model.A;
import org.objectstream.model.B;

import static junit.framework.Assert.assertEquals;

public class ITSimpleThreeClasses {

    private ObjectStreamManager manager;
    private ObjectStream stream;

    @Before
    public void setup() {
        manager = new DefaultObjectStreamManager();
        stream = manager.create();
    }

    @After
    public void cleanup() {

    }

    @Test
    public void testSimple() {
        C c = new C();
        c.setValue(1);
        B b = new B();
        b.setValue(10);
        b.setC(c);
        A a = new A();
        a.setValue(100);
        a.setB(b);

        assertEquals(111, a.getResult());
        assertEquals(100, stream.object(a).getValue());
        assertEquals(111, stream.object(a).getResult());

        stream.object(c).setValue(2);
        assertEquals(112, stream.object(a).getResult());
    }


    public void testSimpleWithListener() {
        C c = new C();
        c.setValue(1);
        B b = new B();
        b.setValue(10);
        b.setC(c);
        A a = new A();
        a.setValue(100);
        a.setB(b);

        assertEquals(111, a.getResult());
        assertEquals(111, stream.object(a).getResult());

        UpdateListener listener = new UpdateListener();

        stream.addListener(listener).to(a).getResult();
        //stream.observe(value(a).getResult(), listener);
      //stream.observe(new Value(new MethodValue(realObj, method, objects, proxyFactory)), listener);
        stream.object(c).setValue(2);

        assertEquals(112, listener.getResult());
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