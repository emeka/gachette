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
import org.objectstream.instrumentation.cglib.CglibProxyFactory;
import org.objectstream.spi.ObjectStreamProvider;
import org.objectstream.spi.simple.DefaultObjectStreamProvider;

public class DefaultObjectStream implements ObjectStream {
    private final ObjectStreamProvider streamProvider;
    private final ProxyFactory proxyFactory;


    public DefaultObjectStream(ObjectStreamProvider streamProvider, ProxyFactory proxyFactory) {
        this.streamProvider = streamProvider;
        this.proxyFactory = proxyFactory;
    }

    @Override
    public <T> T object(T object) {
        return proxyFactory.createObjectProxy(object);
    }

    @Override
    public FluentObserveValue observe() {
        CallContext context = new ThreadLocalCallContext();
        return new FluentObserveValue(streamProvider, context);
    }
}
