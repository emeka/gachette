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
