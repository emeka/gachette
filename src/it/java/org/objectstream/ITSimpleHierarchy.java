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


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ITSimpleHierarchy {


    @Before
    public void setup() {

    }


    @After
    public void cleanup() {

    }

    @Test
    public void testSimple() {
        Trade trade1 = new Trade(100);
        Trade trade2 = new Trade(200);
        Portfolio portfolio = new Portfolio();

        portfolio.addTrade(trade1);
        portfolio.addTrade(trade2);

        assertEquals(300, portfolio.getSpotPrice());

    }

    private class Trade {
        private long spotPrice;

        public Trade(long spotPrice){
            this.spotPrice = spotPrice;
        }

        long getSpotPrice() {
            return spotPrice;
        }
    }

    private class Portfolio {
        private Set<Trade> trades = new HashSet();


        public void addTrade(Trade t){
            trades.add(t);
        }

        public long getSpotPrice() {
            long spotPrice = 0;
            for (Trade t : trades) {
                spotPrice += t.getSpotPrice();
            }

            return spotPrice;
        }
    }
}
