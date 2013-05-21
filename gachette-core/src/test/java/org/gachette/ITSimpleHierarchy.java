/*
 * Copyright 2013 Emeka Mosanya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gachette;


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
