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

package org.gachette;

import org.gachette.api.FluentObserveValue;
import org.gachette.spi.callprocessor.CallProcessor;
import org.gachette.spi.graphprovider.GraphProvider;

public class DefaultGachette implements Gachette {
    private final CallProcessor callProcessor;
    private final GraphProvider graphProvider;


    public DefaultGachette(CallProcessor callProcessor, GraphProvider graphProvider) {
        this.callProcessor = callProcessor;
        this.graphProvider = graphProvider;
    }

    @Override
    public <T> T object(T object) {
        return callProcessor.createProxy(object);
    }

    @Override
    public FluentObserveValue observe() {
        return new FluentObserveValue(callProcessor, graphProvider);
    }
}
