package org.objectstream.value;

import java.util.Map;

public class ConstantValue implements ValueCalculator<Object> {
    private final Object value;

    public ConstantValue(Object value){
        this.value = value;
    }

    @Override
    public Object calculate(Map<Value, Object> dependencies) {
        return value;
    }

    public int hashCode() {
        return value.hashCode();
    }

    public String toString(){
        return String.format("ConstantValue(%s)", value);
    }
}
