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
import org.objectstream.spi.ObjectStreamProviderHandler;
import org.objectstream.spi.callprocessor.CallProcessor;

import java.lang.reflect.Method;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ObjectStreamProviderHandlerTest {

    private ObjectStreamProviderHandler handler;

    @Mock
    CallProcessor callProcessor;

    @Mock
    Object object, realObject;
    Object[] parameters;
    Method method;

    @Before
    public void setup() throws NoSuchMethodException {
        handler = new ObjectStreamProviderHandler(realObject, callProcessor);
        parameters = new Object[]{};
        method = TestClass.class.getMethod("getValue", null);
    }

    @Test
    public void testGetPropertyNewValueEmptyValueStack() {
        handler.handle(object, method, parameters);

        verify(callProcessor).eval(realObject, method, parameters);
    }

    private static class TestClass {
        public int getValue() {
            return 0;
        }
    }
}