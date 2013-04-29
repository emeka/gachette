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
import org.objectstream.context.CallContext;

import java.lang.reflect.Method;
import java.util.Stack;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContextualHandlerTest {

    private ContextualHandler contextualHandler;

    @Mock
    Object object;

    @Mock
    CallContext context;

    @Mock
    MethodHandler defaultMethodHandler;

    @Mock
    MethodHandler contextMethodHandler;

    @Mock
    Stack<MethodHandler> methodHandlerStack;

    Method method = this.getClass().getMethods()[0];
    Object[] parameters = new Object[]{};

    @Before
    public void setup() {
        contextualHandler = new ContextualHandler(object, context, defaultMethodHandler);
    }

    @Test
    public void testDefaultHandler() {
        when(context.getMethodHandlerStack()).thenReturn(methodHandlerStack);
        when(methodHandlerStack.empty()).thenReturn(true);

        contextualHandler.handle(null, method, parameters);

        verify(defaultMethodHandler).handle(object,method,parameters);
        verify(contextMethodHandler,never()).handle(object,method,parameters);
    }

    @Test
    public void testContextHandler() {
        when(context.getMethodHandlerStack()).thenReturn(methodHandlerStack);
        when(methodHandlerStack.empty()).thenReturn(false);

        when(methodHandlerStack.peek()).thenReturn(contextMethodHandler);
        contextualHandler.handle(null, method, parameters);

        verify(contextMethodHandler).handle(object,method,parameters);
        verify(defaultMethodHandler,never()).handle(object,method,parameters);
    }
}