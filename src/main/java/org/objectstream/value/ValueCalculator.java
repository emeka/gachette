package org.objectstream.value;


import java.util.Map;

public interface ValueCalculator<T> {
    public T calculate(Map<Value,Object> dependencies);
}
