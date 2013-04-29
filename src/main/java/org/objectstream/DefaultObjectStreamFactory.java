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

import org.objectstream.context.ThreadLocalCallContext;
import org.objectstream.instrumentation.cglib.CglibProxyFactory;
import org.objectstream.spi.DefaultObjectStreamProvider;
import org.objectstream.spi.ObjectStreamProvider;
import org.objectstream.spi.simple.CollectionStreamProvider;

public class DefaultObjectStreamFactory implements ObjectStreamFactory {
    @Override
    public ObjectStream create(){
        CglibProxyFactory proxyFactory = new CglibProxyFactory();
        CollectionStreamProvider streamProvider = new CollectionStreamProvider();
        ObjectStreamProvider objectStreamProvider = new DefaultObjectStreamProvider(streamProvider,proxyFactory,new ThreadLocalCallContext());
        DefaultObjectStream objectStream = new DefaultObjectStream(objectStreamProvider);
        return objectStream;
    }
}
