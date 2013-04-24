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


import org.objectstream.context.CallContext;

import java.lang.reflect.Method;

public class ContextualHandler<T> implements MethodHandler {
    private final T realObj;
    private final CallContext context;
    private final MethodHandler defaultHandler;

    public ContextualHandler(T realObj, CallContext context, MethodHandler defaultHandler) {
        this.realObj = realObj;
        this.context = context;
        this.defaultHandler = defaultHandler;
    }

    public Object handle(Object o, Method method, Object[] objects) {
        MethodHandler handler;

        if(context.getMethodHandlerStack().empty()){
            handler = defaultHandler;
        } else {
            handler = context.getMethodHandlerStack().peek();
        }

        return handler.handle(realObj, method, objects);
    }
}
