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
import org.gachette.value.ValueObserver;

/**
 * User Gachette API.
 *
 * The API to Gachette in divided in two parts:
 * <ul>
 *     <li>the Gachette interface to create Gachette proxy and register observers and,</li>
 *     <li>the access to Gachette through each Gachette proxy when getting and setting values.</li>
 * </ul>
 *
 * Here are example of interaction with Gachette:
 *                        
 * <code>stream.object(new A());</code> will create a proxy around an new instance of class A.
 * <p>
 * <code>stream.observe().value(a.getResult()).with(observer);</code>  will register an observer that will be called
 * each time the value <code>a.getResult()</code> changes.
 * <p>
 * <code>a.getResult();</code> with the the value of applying the method <code>getResult()</code> on object a using
 * the cached value if available.  The resulting value will be cached until invalidated.
 * <p>
 * <code>a.setX(100);</code> will set a new value to the field <code>X</code> and potentially will trigger the
 * recalculation of any value depending on it.
 **/
public interface Gachette {

    /**
     * Create an Gachette proxy from an object.
     *
     * This method will proxy an existing object and make it implement
     * {@link org.gachette.instrumentation.GachetteProxy} marker interface.
     * Any object reference by Gachette must be an GachetteProxy
     *
     * Example: A a = stream.object(new A());
     *
     * @param object
     * @param <T>
     * @return  A proxy of the given object implementing GachetteProxy marker interface.
     */
    <T> T object(T object);


    /**
     * The Gachette.observe command will all an observer to a value using a fluent interface.
     *
     * Example: stream.observe().value(a.getResult()).with(observer)
     *
     * where a is a proxied stream object which has been created using stream.object() and
     *       observer is a {@link ValueObserver} object.
     *
     * @return the FluentObserveValue object used in the fluent api.
     */
    FluentObserveValue observe();
}
