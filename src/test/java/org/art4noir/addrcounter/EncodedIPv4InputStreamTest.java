package org.art4noir.addrcounter;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;

public class EncodedIPv4InputStreamTest {
    @Test(expected = EOFException.class)
    public void testEmpty() throws IOException {
        Assert.assertEquals(0, encodeOnce(""));
    }

    @Test(expected = EOFException.class)
    public void testIncomplete() throws IOException {
        Assert.assertEquals(0, encodeOnce("0.0.0"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrect1() throws IOException {
        Assert.assertEquals(0, encodeOnce("132412.1231.1231.123123"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrect2() throws IOException {
        Assert.assertEquals(0, encodeOnce("1.2.333.4"));
    }

    @Test
    public void testZeros() throws IOException {
        Assert.assertEquals(0, encodeOnce("0.0.0.0"));
    }

    @Test
    public void testZerosWithEOL() throws IOException {
        Assert.assertEquals(0, encodeOnce("0.0.0.0\n"));
    }

    @Test
    public void testMiddle1() throws IOException {
        Assert.assertEquals(Integer.MAX_VALUE, encodeOnce("127.255.255.255"));
    }

    @Test
    public void testMiddle2() throws IOException {
        Assert.assertEquals(Integer.MIN_VALUE, encodeOnce("128.0.0.0"));
    }

    @Test
    public void testLast() throws IOException {
        Assert.assertEquals(-1, encodeOnce("255.255.255.255"));
    }

    private int encodeOnce(String data) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(data.getBytes(Charsets.UTF_8));
        EncodedIPv4InputStream encoder = new EncodedIPv4InputStream(stream);
        return encoder.readEncodedIp();
    }

    @Test
    public void testMultipleLines() throws IOException {
        byte[] data = String.join(
                "\n",
                "0.1.3.42",
                "0.1.1.2",
                "255.1.3.42",
                "64.131.32.42"
        ).getBytes(Charsets.UTF_8);
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        EncodedIPv4InputStream encoder = new EncodedIPv4InputStream(stream);
        Assert.assertEquals(ipAsInt(0, 1, 3, 42), encoder.readEncodedIp());
        Assert.assertEquals(ipAsInt(0, 1, 1, 2), encoder.readEncodedIp());
        Assert.assertEquals(ipAsInt(255, 1, 3, 42), encoder.readEncodedIp());
        Assert.assertEquals(ipAsInt(64, 131, 32, 42), encoder.readEncodedIp());
    }

    private long ipAsInt(int... parts) {
        Preconditions.checkArgument(parts.length == 4);
        for (int part : parts) {
            Preconditions.checkArgument(0 <= part && part <= 256);
        }
        long p = parts[0];
        for (int i = 1; i < parts.length; ++i) {
            p = p * 256 + parts[i];
        }
        Preconditions.checkState(p >> 32 == 0);
        return (int) p;
    }
}
