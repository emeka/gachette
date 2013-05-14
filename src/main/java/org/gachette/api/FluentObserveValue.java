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

package org.gachette.api;

import org.gachette.spi.callprocessor.CallProcessor;
import org.gachette.spi.graphprovider.GraphProvider;
import org.gachette.value.Value;

public class FluentObserveValue {
    private static final String ERROR_BLURB =
            "Please ensure that stream.observe().value() takes a method call to an Gachette object as parameter," +
            "for example stream.observe().value(foo.getResult()).with(observer) with foo an Gachette proxy";

    private final CallProcessor callProcessor;
    private final GraphProvider graphProvider;

    public FluentObserveValue(CallProcessor stream, GraphProvider graphProvider) {
        this.callProcessor = stream;
        this.graphProvider = graphProvider;
    }

    public FluentObserveWith value(Object methodCall) {
        Value value = callProcessor.getContext().getLastValue();

        if(value == null){
            throw new RuntimeException("Context error: value not found. " + ERROR_BLURB);
        }

        return new FluentObserveWith(graphProvider, value);
    }
}
