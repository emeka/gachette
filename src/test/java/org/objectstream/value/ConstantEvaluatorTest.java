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

package org.objectstream.value;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ConstantEvaluatorTest {
    ConstantEvaluator constantEvaluator;

    @Test
    public void test(){
        assertEquals(10, (new ConstantEvaluator<Integer>(10)).eval().intValue());
    }

    @Test
    public void testSameValue(){
        ConstantEvaluator constantEvaluator1 = new ConstantEvaluator<Integer>(10);
        ConstantEvaluator constantEvaluator2 = new ConstantEvaluator<Integer>(10);

        assertEquals(constantEvaluator1.hashCode(), constantEvaluator2.hashCode());
        assertEquals(constantEvaluator1, constantEvaluator2);
    }

    @Test
    public void testDifferentValue(){
        ConstantEvaluator constantEvaluator1 = new ConstantEvaluator<Integer>(10);
        ConstantEvaluator constantEvaluator2 = new ConstantEvaluator<Integer>(20);

        assertNotEquals(constantEvaluator1.hashCode(), constantEvaluator2.hashCode());
        assertNotEquals(constantEvaluator1, constantEvaluator2);
    }

    @Test
    public void testToString(){
        ConstantEvaluator constantEvaluator = new ConstantEvaluator<Integer>(10);
        assertTrue(constantEvaluator.toString().startsWith("Constant("));
    }
}
