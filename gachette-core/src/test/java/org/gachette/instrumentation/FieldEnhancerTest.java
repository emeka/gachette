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

package org.gachette.instrumentation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.gachette.spi.callprocessor.CallProcessor;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FieldEnhancerTest {

    FieldEnhancer fieldEnhancer;

    @Mock
    CallProcessor callProcessor;

    Object nonPrimitive = new Object();
    Object state = new Object();

    @Before
    public void setup(){
        fieldEnhancer = new FieldEnhancer(callProcessor);
    }

    @Test
    public void test(){
        TestClass testObject = new TestClass();
        testObject.setPrimitive(100);
        testObject.setNonPrimitive(nonPrimitive);
        testObject.modify(state);

        fieldEnhancer.enhance(testObject);

        verify(callProcessor).createProxy(nonPrimitive);
        verify(callProcessor,never()).createProxy(state);
    }

    private static class TestClass {
        private int primitive;
        private Object nonPrimitive;
        private Object state;

        public void setPrimitive(int primitive) {
            this.primitive = primitive;
        }

        public int getPrimitive() {
            return primitive;
        }

        public void setNonPrimitive(Object nonPrimitive) {
            this.nonPrimitive = nonPrimitive;
        }

        public Object getNonPrimitive() {
            return nonPrimitive;
        }

        public void modify(Object state) {
            this.state = state;
        }

        public Object state() {
            return this.state;
        }
    }
}
