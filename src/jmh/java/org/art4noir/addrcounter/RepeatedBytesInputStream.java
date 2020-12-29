package org.art4noir.addrcounter;

import java.io.InputStream;

public class RepeatedBytesInputStream extends InputStream {
    private final byte[] data;
    private int pos;

    public RepeatedBytesInputStream(byte[] data) {
        this.data = data;
    }

    @Override
    public int read() {
        if (pos == data.length) {
            pos = 0;
        }
        return data[pos++];
    }

    @Override
    public int read(byte[] buffer, int off, int len) {
        if (pos == data.length) {
            pos = 0;
        }
        int available = Math.min(data.length - pos, len);
        System.arraycopy(data, pos, buffer, off, available);
        pos += available;
        return available;
    }
}