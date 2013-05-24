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

package org.gachette.spi.graphprovider;

import org.gachette.spi.callprocessor.CallProcessor;
import org.gachette.value.Evaluator;
import org.gachette.value.Value;
import org.gachette.value.ValueObserver;

import java.lang.reflect.Method;

public interface GraphProvider {
    Value value(Object object, Method method, Object[] parameters, CallProcessor callProcessor);

    <M> void observe(Value<M> value, ValueObserver<M> observer); //

    void bind(Value parent, Value child);

    void unbind(Value parent, Value child);

    void unbind(Object parent, Object child);

    void invalidate(Value value);

    void invalidate(Object object);

    void notifyChange(Value value);
}
