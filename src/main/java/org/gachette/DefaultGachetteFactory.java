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

import org.gachette.context.ThreadLocalCallContext;
import org.gachette.instrumentation.cglib.CglibProxyFactory;
import org.gachette.spi.callprocessor.CallProcessor;
import org.gachette.spi.callprocessor.DefaultCallProcessor;
import org.gachette.spi.graphprovider.collection.CollectionGraphProvider;

public class DefaultGachetteFactory implements GachetteFactory {
    @Override
    public Gachette create(){
        CglibProxyFactory proxyFactory = new CglibProxyFactory();
        CollectionGraphProvider graphProvider = new CollectionGraphProvider();
        CallProcessor callProcessor = new DefaultCallProcessor(graphProvider, proxyFactory, new ThreadLocalCallContext());
        DefaultGachette gachette = new DefaultGachette(callProcessor, graphProvider);
        return gachette;
    }
}
