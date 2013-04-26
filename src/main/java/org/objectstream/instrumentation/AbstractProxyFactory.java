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
import org.objectstream.context.ThreadLocalCallContext;
import org.objectstream.spi.ObjectStreamProvider;

public abstract class AbstractProxyFactory implements ProxyFactory {
    private final ObjectStreamProvider streamProvider;

    public AbstractProxyFactory(ObjectStreamProvider streamProvider) {
        this.streamProvider = streamProvider;
    }

    public <T> T instrumentField(T object){
        ObjectInstrumentor enhancer = new FieldInstrumentor(this);
        return enhancer.enhance(object);
    }

    public <T> T createObjectProxy(T object) {
        if(object instanceof ObjectStreamProxy){
            return object;
        }
        CallContext context = new ThreadLocalCallContext();
        ProxyProvider<T> pf = getProxyFactory(new ContextualHandler<>(object, context,new EvalHandler<>(streamProvider, this, context)));
        return pf.create(object);
    }

    protected abstract <T> ProxyProvider<T> getProxyFactory(MethodHandler interceptor);
}
