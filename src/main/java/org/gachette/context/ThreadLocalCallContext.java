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

package org.gachette.context;


import org.gachette.value.Value;

public class ThreadLocalCallContext implements CallContext {

    public static final ThreadLocal<CallContext> threadLocalCallContext = new ThreadLocal<CallContext>() {
        @Override
        protected CallContext initialValue() {
            return new DefaultCallContext(){};
        }
    };

    @Override
    public Value getLastValue() {
        return threadLocalCallContext.get().getLastValue();
    }

    @Override
    public void push(Value value) {
        threadLocalCallContext.get().push(value);
    }

    @Override
    public Value peek() {
        return threadLocalCallContext.get().peek();
    }

    @Override
    public Value pop() {
        return threadLocalCallContext.get().pop();
    }

    @Override
    public boolean empty() {
        return threadLocalCallContext.get().empty();
    }

    @Override
    public void reset() {
        threadLocalCallContext.get().reset();
    }

    @Override
    public int depth() {
        return threadLocalCallContext.get().depth();
    }
}
