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

package org.objectstream.api;

import org.objectstream.context.CallContext;
import org.objectstream.spi.ObjectStreamProvider;
import org.objectstream.value.Value;

public class FluentObserveValue {
    private static final String ERROR_BLURB =
            "Please ensure that stream.observe().value() takes a method call to an ObjectStream object as parameter," +
            "for example stream.observe().value(foo.getResult()).with(observer) with foo an ObjectStream proxy";

    private final ObjectStreamProvider streamProvider;
    private final CallContext context;

    public FluentObserveValue(ObjectStreamProvider stream, CallContext context) {
        this.streamProvider = stream;
        this.context = context;
    }

    public FluentObserveWith value(Object methodCall) {
        Value value = context.getLastValue();

        if(value == null){
            throw new RuntimeException("Context error: value not found. " + ERROR_BLURB);
        }

        //We have what we are looking for, so we can reset the context.
        context.reset();

        return new FluentObserveWith(streamProvider, value);
    }
}
