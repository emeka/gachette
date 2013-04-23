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
