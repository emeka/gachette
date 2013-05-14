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


import org.junit.Before;
import org.junit.Test;
import org.objectstream.model.A;
import org.objectstream.model.B;
import org.objectstream.model.D;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class ITClassesWithCollections {

    private ObjectStreamFactory manager;
    private ObjectStream stream;
    private A a1, a2;
    private B b1, b2;
    private D d;
    private Collection<A> collA;

    @Before
    public void setup() {
        manager = new DefaultObjectStreamFactory();
        stream = manager.create();

        a1 = stream.object(new A());
        a2 = stream.object(new A());
        b1 = stream.object(new B());
        b2 = stream.object(new B());
        d = stream.object(new D());

        collA = stream.object(new ArrayList<A>());
    }

    @Test
    public void testCollectionBeforeProxying() {
        a1 = new A();
        a2 = new A();
        b1 = new B();
        b2 = new B();
        d = new D();

        b1.setValue(1);
        a1.setValue(10);
        a1.setB(b1);

        b2.setValue(2);
        a2.setValue(20);
        a2.setB(b2);

        d.setValue(100);

        assertEquals(100, stream.object(d).getResult());
        stream.object(d).setCollection(collA);
        assertEquals(100, stream.object(d).getResult());

        stream.object(collA).add(a1);
        assertEquals(111, stream.object(d).getResult());

        stream.object(collA).add(a2);
        assertEquals(133, stream.object(d).getResult());

        stream.object(collA).remove(a1);
        assertEquals(122, stream.object(d).getResult());

        stream.object(d).setCollection(null);
        assertEquals(100, stream.object(d).getResult());
    }

    @Test
    public void testCollectionOnProxy() {
        b1.setValue(1);
        a1.setValue(10);
        a1.setB(b1);

        b2.setValue(2);
        a2.setValue(20);
        a2.setB(b2);

        d.setValue(100);

        assertEquals(100, d.getResult());
        d.setCollection(collA);
        assertEquals(100, d.getResult());

        collA.add(a1);
        assertEquals(111, d.getResult());

        collA.add(a2);
        assertEquals(133, d.getResult());

        collA.remove(a1);
        assertEquals(122, d.getResult());

        d.setCollection(null);
        assertEquals(100, d.getResult());
    }
}
