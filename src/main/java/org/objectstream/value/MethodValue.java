package org.objectstream.value;

import org.objectstream.instrumentation.FieldEnhancer;
import org.objectstream.instrumentation.ObjectEnhancer;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;


public class MethodValue<T> implements ValueCalculator<T> {

    private final Object object;
    private final Method method;
    private final Object[] parameters;

    public MethodValue(Object object, Method method, Object[] parameters) {
        this.object = object;
        this.method = method;
        this.parameters = parameters;
    }

    @Override
    public T calculate(Map<Value, Object> dependencies) {
        ObjectEnhancer enhancer = new FieldEnhancer();  //TODO: remove the new.
        enhancer.enhance(object);

        T result;
        try {
           result = (T) method.invoke(object,parameters);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + object.hashCode();
        hash = hash * 31 + method.hashCode();
        hash = hash * 13 + Arrays.hashCode(parameters);
        return hash;
    }
}
