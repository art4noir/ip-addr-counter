package org.art4noir.addrcounter;

import org.junit.Assert;
import org.junit.Test;

public class IPv4CounterTest {
    @Test
    public void testSeveralValues() {
        IPv4Counter counter = new IPv4Counter();
        counter.add(-1123641263);
        counter.add(1123641263);
        counter.add(723641263);
        counter.add(15645243);
        counter.add(1645243);
        counter.add(145243);
        counter.add(14243);
        counter.add(1443);
        counter.add(145);
        counter.add(11);
        counter.add(0);
        Assert.assertEquals(11, counter.getCount());
    }

    @Test
    public void testAllValues() {
        IPv4Counter counter = new IPv4Counter();
        for (long i = Integer.MIN_VALUE; i <= Integer.MAX_VALUE; ++i) {
            counter.add((int) i);
        }
        Assert.assertEquals(0x1_00_00_00_00L, counter.getCount());
    }
}
