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

package org.gachette.instrumentation;

import org.gachette.exceptions.ExceptionUtils;
import org.gachette.spi.callprocessor.CallProcessor;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class FieldEnhancer implements ObjectEnhancer {


    private final CallProcessor callProcessor;

    public FieldEnhancer(CallProcessor callProcessor) {
        this.callProcessor = callProcessor;
    }

    @Override
    public <T> T enhance(T object) {
        //1. get the list of object properties (we do not work work with basic types yet)
        //2. for each property, replace with the proxy.
        try {
            for (PropertyDescriptor propertyDescriptor :
                    Introspector.getBeanInfo(object.getClass(), Object.class).getPropertyDescriptors()) {
                Method read = propertyDescriptor.getReadMethod();
                Method write = propertyDescriptor.getWriteMethod();
                if (!propertyDescriptor.getPropertyType().isPrimitive() && read != null && write != null) {
                    Object originalValue = read.invoke(object);
                    if (originalValue != null && !(originalValue instanceof GachetteProxy)) {
                        Object proxy = callProcessor.createProxy(originalValue);
                        write.invoke(object, proxy);
                    }
                }
            }
        } catch (Throwable e) {
            throw ExceptionUtils.wrap(e);
        }

        return object;
    }
}
