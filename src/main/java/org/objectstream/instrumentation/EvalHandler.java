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

package org.objectstream.instrumentation;


import org.objectstream.context.CallContext;
import org.objectstream.spi.ObjectStreamProvider;
import org.objectstream.value.MethodValue;
import org.objectstream.value.Value;

import java.lang.reflect.Method;

public class EvalHandler<T> implements MethodHandler {
    private final ObjectStreamProvider streamProvider;
    private final ProxyFactory proxyFactory;
    private final CallContext context;

    public EvalHandler(ObjectStreamProvider stream, ProxyFactory proxyFactory, CallContext context) {
        this.streamProvider = stream;
        this.proxyFactory = proxyFactory;
        this.context = context;
    }

    public Object handle(Object object, Method method, Object[] objects) {

        Object res = null;
        if (method.getReturnType() != Void.TYPE) {
            Value value = streamProvider.value(new MethodValue(object, method, objects, proxyFactory));
            context.getValueStack().push(value);

            res = value.getValue(); //this call must be between the push and the pop

            context.getValueStack().pop();
            if (!context.getValueStack().empty()) {
                streamProvider.bind(context.getValueStack().peek(), value);
            }
        } else {
            try {
                res = method.invoke(object, objects);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return res;
    }
}
