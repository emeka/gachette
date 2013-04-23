package org.objectstream.instrumentation;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class FieldInstrumentor implements ObjectInstrumentor {

    private final ProxyFactory proxyFactory;

    public FieldInstrumentor(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    @Override
    public Object enhance(Object object) {
        //1. get the list of object properties (we do not work work with basic types yet)
        //2. for each property, replace with the proxy.

        try {
            for (PropertyDescriptor propertyDescriptor :
                    Introspector.getBeanInfo(object.getClass(), Object.class).getPropertyDescriptors()) {
                System.out.println(propertyDescriptor);
                Method read = propertyDescriptor.getReadMethod();
                Method write = propertyDescriptor.getWriteMethod();
                if (!propertyDescriptor.getPropertyType().isPrimitive() && read != null && write != null) {
                    Object originalValue = null;
                    try {
                        originalValue = read.invoke(object);
                        if (originalValue != null) {
                            Object proxiedValue = proxyFactory.createObjectProxy(originalValue);
                            write.invoke(object, proxiedValue);
                        }
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }

        return object;
    }
}
