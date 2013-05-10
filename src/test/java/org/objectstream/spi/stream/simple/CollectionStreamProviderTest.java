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

package org.objectstream.spi.stream.simple;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.objectstream.context.CallContext;
import org.objectstream.instrumentation.ProxyFactory;
import org.objectstream.spi.graphprovider.collection.CollectionGraphProvider;
import org.objectstream.value.Evaluator;
import org.objectstream.value.Value;
import org.objectstream.value.ValueObserver;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CollectionStreamProviderTest {

    private CollectionGraphProvider streamProvider;

    @Mock
    ProxyFactory proxyFactory;

    @Mock
    CallContext context;

    @Mock
    Evaluator evaluator1, evaluator2;

    @Mock
    ValueObserver observer;

    @Mock
    Value unknownValue;

    @Before
    public void setup() {
        streamProvider = new CollectionGraphProvider();
        when(evaluator1.eval(anyObject(), anyBoolean())).thenReturn(null);
        when(evaluator2.eval(anyObject(), anyBoolean())).thenReturn(null);
    }

    @Test
    public void testValue() {
        Value value1 = streamProvider.value(evaluator1);

        //calling twice with the same evaluator should return the same value
        assertEquals(value1, streamProvider.value(evaluator1));

        Value value2 = streamProvider.value(evaluator2);
        assertNotSame(value1, value2);
    }

    @Test
    public void testObserveAndNotify() {
        Value value1 = streamProvider.value(evaluator1);

        streamProvider.observe(value1, observer); //should notify value1
        streamProvider.observe(value1, observer); //should not notify value1 since observer already exists

        streamProvider.notifyChange(value1); //should notify value1
        streamProvider.notifyChange(value1); //should notify value1
        verify(observer, times(3)).notify(value1);
    }

    @Test
    public void testBindAndUnbind() {
        Value value1 = streamProvider.value(evaluator1);
        Value value2 = streamProvider.value(evaluator2);
        streamProvider.bind(value1, value2);

        streamProvider.observe(value1, observer); //should notify value1

        streamProvider.notifyChange(value2);  //should not notify value1 as notification does not follow binds


        streamProvider.unbind(value1, value2);
        streamProvider.invalidate(value2); //should not notify value1 after the unbind

        verify(observer, times(1)).notify(value1);
    }

    @Test(expected = RuntimeException.class)
    public void testBindUnknownValue(){
        Value value1 = streamProvider.value(evaluator1);
        streamProvider.bind(value1, unknownValue);
    }

    @Test
    public void testInvalidation() {
        Value value1 = streamProvider.value(evaluator1);
        Value value2 = streamProvider.value(evaluator2);

        streamProvider.bind(value1, value2);
        streamProvider.observe(value1, observer); //should notify value1

        value1.eval();
        value2.eval();

        assertFalse(value1.isDirty());
        assertFalse(value2.isDirty());

        streamProvider.invalidate(value2);  //should notify as invalidation follow binds and value changes trigger observers
        verify(observer, times(2)).notify(value1);

        assertTrue(value1.isDirty());
        assertTrue(value2.isDirty());
    }
}
