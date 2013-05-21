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

import java.util.Stack;

public abstract class DefaultCallContext implements CallContext {
    private Value lastValue;
    private final Stack<Value> valueStack = new Stack<>();

    @Override
    public void reset() {
        valueStack.clear();
        lastValue = null;
    }

    @Override
    public Value getLastValue() {
        return lastValue;
    }

    @Override
    public void push(Value value) {
        valueStack.push(value);
        lastValue = value;
    }

    @Override
    public Value peek() {
        return valueStack.peek();
    }

    @Override
    public Value pop() {
        return valueStack.pop();
    }

    @Override
    public boolean empty() {
        return valueStack.empty();
    }

    @Override
    public int depth(){
        return valueStack.size();
    }
}
