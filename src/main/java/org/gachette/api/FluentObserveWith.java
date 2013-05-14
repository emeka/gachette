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

import org.gachette.spi.graphprovider.GraphProvider;
import org.gachette.value.Value;
import org.gachette.value.ValueObserver;

public class FluentObserveWith {
    private final GraphProvider graphProvider;
    private final Value value;
    
    public FluentObserveWith(GraphProvider graphProvider, Value value){
        this.graphProvider = graphProvider;
        this.value = value;
    }
    
    public void with(ValueObserver observer) {
        graphProvider.observe(value, observer);
    }
}
