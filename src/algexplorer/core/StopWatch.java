package algexplorer.core;

/** High-resolution timer. Each method is O(1). */
public class StopWatch {
    private long startTime;
    private long elapsed;

    public void start() {
        startTime = System.nanoTime();     // note the clock now
    }

    public void stop() {
        elapsed = System.nanoTime() - startTime;   // time passed since start
    }

    public long getNanos() {               // elapsed time in nanoseconds
        return elapsed;
    }

    public double getMillis() {            // elapsed time in milliseconds
        return elapsed / 1_000_000.0;
    }
}