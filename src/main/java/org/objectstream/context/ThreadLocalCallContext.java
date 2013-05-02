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

package org.objectstream.context;


import org.objectstream.value.Value;

public class ThreadLocalCallContext implements CallContext {

    public static final ThreadLocal<CallContext> threadLocalCallContext = new ThreadLocal<CallContext>() {
        @Override
        protected CallContext initialValue() {
            return new DefaultCallContext(){};
        }
    };

    @Override
    public Value getLastValue() {
        return threadLocalCallContext.get().getLastValue();
    }

    @Override
    public void push(Value value) {
        threadLocalCallContext.get().push(value);
    }

    @Override
    public Value peek() {
        return threadLocalCallContext.get().peek();
    }

    @Override
    public Value pop() {
        return threadLocalCallContext.get().pop();
    }

    @Override
    public boolean empty() {
        return threadLocalCallContext.get().empty();
    }

    @Override
    public void reset() {
        threadLocalCallContext.get().reset();
    }

    @Override
    public int depth() {
        return threadLocalCallContext.get().depth();
    }
}
