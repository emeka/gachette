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

package org.objectstream;

import org.objectstream.api.FluentObserveValue;
import org.objectstream.context.CallContext;
import org.objectstream.context.ThreadLocalCallContext;
import org.objectstream.instrumentation.ProxyFactory;
import org.objectstream.instrumentation.ValueHandler;
import org.objectstream.spi.ObjectStreamProvider;

public class DefaultObjectStream implements ObjectStream {

    private ProxyFactory proxyFactory;
    private ObjectStreamProvider streamProvider;

    @Override
    public <T> T object(T object) {
        return proxyFactory.createObjectProxy(object);
    }

    @Override
    public FluentObserveValue observe() {
        CallContext context = new ThreadLocalCallContext();
        //Do not forget to pop the value in the in the next part of the fluent call.
        context.getMethodHandlerStack().push(new ValueHandler<>(streamProvider, proxyFactory, context));
        return new FluentObserveValue(streamProvider, context);
    }
    
    public void setProxyFactory(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }
    
    public void setStreamProvider(ObjectStreamProvider streamProvider) {
        this.streamProvider = streamProvider;
    }
}
