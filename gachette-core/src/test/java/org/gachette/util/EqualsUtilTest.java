package org.gachette.util;

import org.gachette.instrumentation.GachetteProxy;
import org.gachette.utils.EqualsUtil;
import org.gachette.utils.HashCodeUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class EqualsUtilTest {


    private int SEED = 1;

    Object object1;
    Object object2;
    Integer object3;

    Object[] array1;
    Object[] array2;

    @Before
    public void setup() {
        object1 = new Object();
        object2 = new Object();
        object3 = new Integer(10);

        array1 = new Object[]{object1, object2};
        array2 = new Object[]{object1, object3};
    }


    @Test
    public void testSameClass() {
        assertFalse(EqualsUtil.isSameClass(object1, null));
        assertTrue(EqualsUtil.isSameClass(object1, object1));
        assertTrue(EqualsUtil.isSameClass(object1, object2));
        assertFalse(EqualsUtil.isSameClass(object1, object3));
    }

    @Test
    public void testPreviousTestFalse() {
        assertFalse(EqualsUtil.equals(false, null, null));
        assertFalse(EqualsUtil.equals(false, true, true));
        assertFalse(EqualsUtil.equals(false, 'a', 'a'));
        assertFalse(EqualsUtil.equals(false, 100, 100));
        assertFalse(EqualsUtil.equals(false, 100L, 100L));
        assertFalse(EqualsUtil.equals(false, 100.0, 100.0));
        assertFalse(EqualsUtil.equals(false, object1, object1));
        assertFalse(EqualsUtil.equals(false, array1, array1));
        assertFalse(EqualsUtil.same(true, object1, object2));
    }

    @Test
    public void testHash() {
        assertTrue(EqualsUtil.equals(true, null, null));
        assertFalse(EqualsUtil.equals(true, null, object1));

        assertTrue(EqualsUtil.equals(true, true, true));
        assertFalse(EqualsUtil.equals(true, true, false));

        assertTrue(EqualsUtil.equals(true, 'a', 'a'));
        assertFalse(EqualsUtil.equals(true, 'a', 'b'));

        assertTrue(EqualsUtil.equals(true, 100, 100));
        assertFalse(EqualsUtil.equals(true, 100, 200));

        assertTrue(EqualsUtil.equals(true, 100L, 100L));
        assertFalse(EqualsUtil.equals(true, 100L, 200L));

        assertTrue(EqualsUtil.equals(true, 100.0, 100.0));
        assertFalse(EqualsUtil.equals(true, 100.0, 200.0));

        assertTrue(EqualsUtil.equals(true, object1, object1));
        assertFalse(EqualsUtil.equals(true, object1, object2));

        assertTrue(EqualsUtil.equals(true, array1, array1));
        assertFalse(EqualsUtil.equals(true, array1, array2));

        assertTrue(EqualsUtil.same(true, object1, object1));
        assertFalse(EqualsUtil.same(true, object1, object2));

        assertTrue(EqualsUtil.same(true, object1, new TestGachetteProxy(object1)));
        assertFalse(EqualsUtil.same(true, object1, new TestGachetteProxy(object2)));
    }

    @Test
    public void testSameObject() {
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
