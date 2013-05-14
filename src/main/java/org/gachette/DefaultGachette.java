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

package org.gachette;

import org.gachette.api.FluentObserveValue;
import org.gachette.spi.callprocessor.CallProcessor;
import org.gachette.spi.graphprovider.GraphProvider;

public class DefaultGachette implements Gachette {
    private final CallProcessor callProcessor;
    private final GraphProvider graphProvider;


    public DefaultGachette(CallProcessor callProcessor, GraphProvider graphProvider) {
        this.callProcessor = callProcessor;
        this.graphProvider = graphProvider;
    }

    @Override
    public <T> T object(T object) {
        return callProcessor.createProxy(object);
    }

    @Override
    public FluentObserveValue observe() {
        return new FluentObserveValue(callProcessor, graphProvider);
    }
}
