package org.art4noir.addrcounter;

import com.google.common.base.Preconditions;

import java.io.*;

/**
 * Reads list of IPv4 text records with \n as delimiter from stdin or passed file
 * and writes count of unique IP addresses in stdout.
 */
public class App {
    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            File file = new File(args[0]);
            Preconditions.checkState(file.isFile() && file.exists(), "Given file isn't exist");
            try (InputStream stream = new FileInputStream(file)) {
                countUniqueAddresses(stream);
            }
        } else {
            countUniqueAddresses(System.in);
        }
    }

    private static void countUniqueAddresses(InputStream stream) throws IOException {
        EncodedIPv4InputStream ipStream = new EncodedIPv4InputStream(stream, '\n');
        IPv4Counter iPv4Counter = new IPv4Counter();
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                int ipv4Address = ipStream.readEncodedIp();
                iPv4Counter.add(ipv4Address);
            }
        } catch (EOFException e) {
            // as expected
        }
        long count = iPv4Counter.getCount();
        System.out.println(count);
    }
}
