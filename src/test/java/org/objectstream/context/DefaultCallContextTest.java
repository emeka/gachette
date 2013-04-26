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

package org.objectstream.context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.objectstream.instrumentation.MethodHandler;
import org.objectstream.value.Value;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCallContextTest {

    private DefaultCallContext context;

    @Mock
    Value value;

    @Mock
    MethodHandler methodHandler;

    @Before
    public void setup() {
        context = new DefaultCallContext(){};
    }

    @Test
    public void test() {
        assertNotNull(context.getMethodHandlerStack());
        assertNotNull(context.getValueStack());
        assertNull(context.getLastValue());

        assertTrue(context.getMethodHandlerStack().empty());
        assertTrue(context.getValueStack().empty());

        context.getMethodHandlerStack().push(methodHandler);
        context.getValueStack().push(value);
        context.setLastValue(value);


        assertNotNull(context.getLastValue());
        assertFalse(context.getMethodHandlerStack().empty());
        assertFalse(context.getValueStack().empty());

        context.reset();

        assertNull(context.getLastValue());
        assertTrue(context.getMethodHandlerStack().empty());
        assertTrue(context.getValueStack().empty());

    }
}
