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

package org.gachette.value;

public interface Evaluator<T> {

    /**
     * An Evaluator calculate and return a value.  The returned value depends on the currentValue and a dirty flag.
     * The Evaluator decides when to recalculate.  Certain evaluator will recalculate even if the dirty flag is false
     * like for example {@link IteratorEvaluator}.
     *
     * @param value The current value to be returned if the evaluator does not need to recalculate anything.
     * @param dirty True if the current value is dirty.
     * @return either the currentValue or a new calculated value
     */
    T eval(T currentValue, boolean dirty);

    /**
     *
     * @return the target object being evaluated
     */
    Object targetObject();
}
