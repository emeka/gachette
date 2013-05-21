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


public class Value<M> {

    private final Evaluator<M> evaluator;

    private M value;
    private boolean dirty = true;

    public Value(Evaluator evaluator){
        this.evaluator = evaluator;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty() {
        this.dirty = true;
    }

    public M getValue() {
        return value;
    }

    public M eval() {
        value = evaluator.eval(value, dirty);
        dirty = false;

        return value;
    }

    @Override
    public int hashCode() {
        return evaluator.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) return true;
        if (object == null) return false;
        if (this.getClass() != object.getClass()) return false;
        Value other = (Value) object;

        return evaluator.equals(other.evaluator);
    }

    public String toString() {
        return String.format("Value %s = %s (dirty=%s)", evaluator, value, dirty);
    }
}
