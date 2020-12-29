package org.art4noir.addrcounter;

import java.util.BitSet;

/**
 * Stores int-encoded ip addresses in 2 bitsets with 0x80_00_00_00 cardinalities (limited by Integer.MAX_VALUE)
 * Requires slightly more than 512Mb of heap.
 */
public class IPv4Counter {
    private final BitSet lesserAddresses = new BitSet(Integer.MAX_VALUE);
    private final BitSet greaterAddresses = new BitSet(Integer.MAX_VALUE);

    public IPv4Counter() {
    }

    public void add(int ipv4Address) {
        int pos;
        BitSet addressesPart;
        if (ipv4Address < 0) {
            pos = ~ipv4Address;
            addressesPart = greaterAddresses;
        } else {
            pos = ipv4Address;
            addressesPart = lesserAddresses;
        }
        addressesPart.set(pos);
    }

    public long getCount() {
        return getCount(lesserAddresses) + getCount(greaterAddresses);
    }

    private long getCount(BitSet addresses) {
        int cardinality = addresses.cardinality();
        // since max cardinality is Integer.MAX_VALUE + 1, as int it will be represented as Integer.MIN_VALUE
        return cardinality == Integer.MIN_VALUE ? 0x80_00_00_00L : cardinality;
    }
}
