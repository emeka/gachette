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

package org.objectstream.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.objectstream.context.CallContext;
import org.objectstream.spi.ObjectStreamProvider;
import org.objectstream.value.Value;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FluentObserveValueTest {

    @Mock
    ObjectStreamProvider objectStreamProvider;

    @Mock
    CallContext callContext;

    @Mock
    Value value;

    @Test
    public void testValue(){
        when(objectStreamProvider.getContext()).thenReturn(callContext);
        when(callContext.getLastValue()).thenReturn(value);
        assertTrue((new FluentObserveValue(objectStreamProvider)).value(new Object()) instanceof FluentObserveWith);
    }

    @Test(expected = RuntimeException.class)
    public void testException(){
        when(callContext.getLastValue()).thenReturn(null);
        assertTrue((new FluentObserveValue(objectStreamProvider)).value(new Object()) instanceof FluentObserveWith);
    }
}
