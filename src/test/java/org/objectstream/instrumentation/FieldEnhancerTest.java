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

package org.objectstream.instrumentation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.objectstream.spi.callprocessor.CallProcessor;

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
