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

    /**
     * Reads text representation of ipv4 address as 4 consequent bytes packed as int.
     *
     * @return int representation of ipv4 address
     * @throws EOFException if no full record could be obtained from stream
     * @throws IOException  if any IO problems happened
     */
    public int readEncodedIp() throws IOException {
        int read1 = read();
        if (read1 == -1) {
            throw new EOFException();
        }
        return (readIpPart(DOT, read1) << 24) | (readIpPart(DOT, read()) << 16) | (readIpPart(DOT, read()) << 8) | (readIpPart(NEXT_LINE, read()));
    }

    private int readIpPart(char delim, int read1) throws IOException {
        int part;
        if ('0' <= read1 && read1 <= '9') {
            part = read1 - '0';
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