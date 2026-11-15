package algexplorer.core;

/** Bundles an answer with its operation count and running time. */
public class Result {
    public final long value;       // the answer
    public final long operations;  // how many basic steps were counted
    public final long nanos;       // time taken in nanoseconds

    public Result(long value, long operations, long nanos) {
        this.value = value;
        this.operations = operations;
        this.nanos = nanos;
    }
}