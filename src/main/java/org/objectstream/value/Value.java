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

package org.objectstream.value;


import java.util.HashMap;
import java.util.Map;

public class Value<M> {

    private final ValueCalculator<M> calculator;
    private final Map<Value, Object> dependencies = new HashMap<>();

    private M value;
    private boolean dirty = true;

    public Value(ValueCalculator calculator) {
        this(calculator, false);
    }

    public Value(ValueCalculator calculator, boolean calculate){
        this.calculator = calculator;
        if(calculate){
            getValue();
        }
    }

    public boolean isDirty(){
        return dirty;
    }

    public M getValue() {
        if(dirty){
            value = calculateValue(dependencies);
            dirty = false;
        }

        return value;
    }

    public void update(Value dependency) {
        if(dependency.isDirty()){
            throw new RuntimeException("Updating value with a dirty dependency: " + dependency.toString());
        }
        Object currentDependencyValue = dependencies.get(dependency);
        if (currentDependencyValue == null || currentDependencyValue != dependency.getValue()) {
            dependencies.put(dependency, dependency.getValue());
            dirty = true;
        }
    }

    public int hashCode() {
        return calculator.hashCode();
    }

    public String toString(){
        return String.format("%s = %s (dirty=%s)", calculator, value, dirty);
    }

    private M calculateValue(Map<Value,Object> dependencies){
        return calculator.calculate(dependencies);
    }
}
