package algexplorer.structures;

/**
 * The answer of the bracket checker: yes or no, a message, every step, the step
 * count and the time.
 */
public class BracketResult {
    public final boolean balanced;
    public final String message;
    public final BracketStep[] steps;
    public final long operations;
    public final long nanos;

    public BracketResult(boolean balanced, String message, BracketStep[] steps,
            long operations, long nanos) {
        this.balanced = balanced;
        this.message = message;
        this.steps = steps;
        this.operations = operations;
        this.nanos = nanos;
    }
}