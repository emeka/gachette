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

package org.objectstream.instrumentation.javassist;

import org.objectstream.ObjectStream;
import org.objectstream.instrumentation.AbstractProxyFactory;
import org.objectstream.instrumentation.ListenerInterceptor;
import org.objectstream.instrumentation.MethodInterceptor;
import org.objectstream.instrumentation.ObjectInterceptor;
import org.objectstream.value.ValueObserver;

public class JavassistProxyFactory extends AbstractProxyFactory {
    private final ObjectStream stream;

    public JavassistProxyFactory(ObjectStream stream){
        this.stream = stream;
    }

    public <T,L> T createListenerProxy(T object, ValueObserver<L> observer) {
        JavassistProxy<T> pf = getProxyFactory(new ListenerInterceptor(object, stream, observer, this));
        return pf.create(object);
    }

    public <T> T createObjectProxy(T object) {
        JavassistProxy<T> pf = getProxyFactory(new ObjectInterceptor<>(object, stream, this));
        return pf.create(object);
    }

    private static <T> JavassistProxy<T> getProxyFactory(MethodInterceptor interceptor) {
        return new JavassistProxy<T>(interceptor);
    }
}
