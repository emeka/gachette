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


import org.junit.Before;
import org.junit.Test;
import org.gachette.model.A;
import org.gachette.model.B;
import org.gachette.model.D;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class ITClassesWithCollections {

    private GachetteFactory manager;
    private Gachette stream;
    private A a1, a2;
    private B b1, b2;
    private D d;
    private Collection<A> collA;

    @Before
    public void setup() {
        manager = new DefaultGachetteFactory();
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
