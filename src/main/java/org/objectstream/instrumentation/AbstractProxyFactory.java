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


import org.objectstream.spi.ObjectStreamProvider;

public abstract class AbstractProxyFactory implements ProxyFactory {
    private ObjectStreamProvider streamProvider;

    public <T> T instrumentField(T object){
        ObjectInstrumentor<T> enhancer = new FieldInstrumentor(this);
        return enhancer.enhance(object);
    }

    public <T> T createObjectProxy(T object) {
        if(object instanceof ObjectStreamProxy){
            return object;
        }
        ProxyProvider<T> pf = getProxyFactory(new ObjectInterceptor<>(object, streamProvider, this));
        return pf.create(object);
    }

    public <T> T createValueProxy(T object) {
         if(object instanceof ObjectStreamProxy){
             return object;
         }
         ProxyProvider<T> pf = getProxyFactory(new ValueInterceptor<>(object, streamProvider, this));
         return pf.create(object);
     }

    public void setStreamProvider(ObjectStreamProvider streamProvider) {
        this.streamProvider = streamProvider;
    }

    protected abstract <T> ProxyProvider<T> getProxyFactory(MethodInterceptor interceptor);
}
