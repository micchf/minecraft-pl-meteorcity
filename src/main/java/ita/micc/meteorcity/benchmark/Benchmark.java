package ita.micc.meteorcity.benchmark;

import static java.util.stream.IntStream.rangeClosed;

/**
 * Benchmark Class
 * @author Codeh
 */
public class Benchmark {

    /**
     * Start benchmark
     * @param iterations iterations
     * @return complete's second
     */
    public int start(int iterations) {
        long start = System.currentTimeMillis();
        rangeClosed(1, 50).parallel()
                .forEach(i -> rangeClosed(1, iterations).mapToDouble(Math::sqrt).sum());
        return (int) ((System.currentTimeMillis() - start)/1000);
    }
}
