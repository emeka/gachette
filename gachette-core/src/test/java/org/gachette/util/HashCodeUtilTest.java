package org.gachette.util;

import org.gachette.instrumentation.GachetteProxy;
import org.gachette.utils.HashCodeUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(MockitoJUnitRunner.class)
public class HashCodeUtilTest {


    private int SEED = 1;

    Object object1;
    Object object2;
    Object object3;

    Object[] array1;
    Object[] array2;

    @Before
    public void setup() {
        object1 = new Object();
        object2 = new Object();
        object3 = new Object();

        array1 = new Object[]{object1, object2};
        array2 = new Object[]{object1, object3};
    }


    @Test
    public void testHash() {
        assertEquals(HashCodeUtil.hash(SEED, null), HashCodeUtil.hash(SEED, null));
        assertNotEquals(HashCodeUtil.hash(SEED, null), HashCodeUtil.hash(SEED, object1));

        assertEquals(HashCodeUtil.hash(SEED, true), HashCodeUtil.hash(SEED, true));
        assertNotEquals(HashCodeUtil.hash(SEED, true), HashCodeUtil.hash(SEED, false));

        assertEquals(HashCodeUtil.hash(SEED, 'a'), HashCodeUtil.hash(SEED, 'a'));
        assertNotEquals(HashCodeUtil.hash(SEED, 'a'), HashCodeUtil.hash(SEED, 'b'));

        assertEquals(HashCodeUtil.hash(SEED, 100), HashCodeUtil.hash(SEED, 100));
        assertNotEquals(HashCodeUtil.hash(SEED, 100), HashCodeUtil.hash(SEED, 200));

        assertEquals(HashCodeUtil.hash(SEED, 100L), HashCodeUtil.hash(SEED, 100L));
        assertNotEquals(HashCodeUtil.hash(SEED, 100L), HashCodeUtil.hash(SEED, 200L));

        assertEquals(HashCodeUtil.hash(SEED, 100.0), HashCodeUtil.hash(SEED, 100.0));
        assertNotEquals(HashCodeUtil.hash(SEED, 100.0), HashCodeUtil.hash(SEED, 200.0));

        assertEquals(HashCodeUtil.hash(SEED, object1), HashCodeUtil.hash(SEED, object1));
        assertNotEquals(HashCodeUtil.hash(SEED, object1), HashCodeUtil.hash(SEED, object2));

        assertEquals(HashCodeUtil.hash(SEED, array1), HashCodeUtil.hash(SEED, array1));
        assertNotEquals(HashCodeUtil.hash(SEED, array1), HashCodeUtil.hash(SEED, array2));
    }

    @Test
    public void testIndentityHash() {
        assertEquals(HashCodeUtil.identityHash(object1), HashCodeUtil.identityHash(new TestGachetteProxy(object1)));
        assertNotEquals(HashCodeUtil.identityHash(object1), HashCodeUtil.identityHash(new TestGachetteProxy(object2)));
    }

    private static class TestGachetteProxy implements GachetteProxy {

        private final Object originaObject;

        public TestGachetteProxy(Object originaObject) {
            this.originaObject = originaObject;
        }

        @Override
        public Object getOriginalObject() {
            return originaObject;
        }
    }
}
