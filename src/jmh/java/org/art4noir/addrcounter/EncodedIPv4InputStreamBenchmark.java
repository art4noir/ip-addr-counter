package org.art4noir.addrcounter;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.*;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

@State(Scope.Thread)
public class EncodedIPv4InputStreamBenchmark {
    EncodedIPv4InputStream stream;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (GZIPInputStream in = new GZIPInputStream(new FileInputStream("ip_addresses.gz"), 64 * 1024)) {
            try (PrintWriter p = new PrintWriter(new OutputStreamWriter(out))) {
                Scanner scanner = new Scanner(in);
                for (int i = 0; i < 500000; ++i) {
                    String line = scanner.nextLine();
                    p.println(line);
                }
            }
        }
        byte[] data = out.toByteArray();
        stream = new EncodedIPv4InputStream(new RepeatedBytesInputStream(data));
    }

    @Benchmark
    public void readIp(Blackhole blackhole) throws IOException {
        int encodedIp = stream.readEncodedIp();
        blackhole.consume(encodedIp);
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(EncodedIPv4InputStreamBenchmark.class.getSimpleName())
                .jvmArgs("-Xmx2g")
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
