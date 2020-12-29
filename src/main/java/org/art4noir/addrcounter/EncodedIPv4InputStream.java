package org.art4noir.addrcounter;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reads stream of ip records in form '[0-255].[0-255].[0-255].[0-255]\n' and converts them to int.
 * Uses inner buffer to evade need to frequently call byte-returning versions read() (profiling shows
 * that local version of read is inlined by JIT compiler).
 */
public class EncodedIPv4InputStream extends InputStream {
    private static final char DOT = '.';
    private static final char NEXT_LINE = '\n';

    private final byte[] buffer = new byte[32 * 1024];
    private int bufferPos;
    private int bufferFilled;

    private final InputStream stream;

    public EncodedIPv4InputStream(InputStream stream) {
        this.stream = stream;
    }

    public int readEncodedIp() throws IOException {
        return (readIpPart(DOT) << 24) | (readIpPart(DOT) << 16) | (readIpPart(DOT) << 8) | (readIpPart(NEXT_LINE));
    }

    private int readIpPart(char delim) throws IOException {
        int read1 = read();
        int part;
        if ('0' <= read1 && read1 <= '9') {
            part = read1 - '0';
        } else if (read1 == -1) {
            throw new EOFException();
        } else {
            throw new IllegalArgumentException();
        }

        int read2 = read();
        if ('0' <= read2 && read2 <= '9') {
            part = 10 * part + (read2 - '0');
        } else if (read2 == delim || read2 == -1) {
            return part;
        } else {
            throw new IllegalArgumentException();
        }

        int read3 = read();
        if ('0' <= read3 && read3 <= '9') {
            part = 10 * part + (read3 - '0');
        } else if (read3 == delim || read3 == -1) {
            return part;
        } else {
            throw new IllegalArgumentException();
        }

        int read4 = read();
        if ((read4 == delim || read4 == -1) && part < 256) {
            return part;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int read() throws IOException {
        if (bufferPos < bufferFilled) {
            return buffer[bufferPos++];
        }
        if (bufferFilled == -1) {
            return -1;
        }
        bufferFilled = stream.read(buffer);
        if (bufferFilled == -1) {
            return -1;
        }
        bufferPos = 0;
        return buffer[bufferPos++];
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}