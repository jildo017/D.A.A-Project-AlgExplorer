package algexplorer.recursion;

/**
 * The answer from the Hanoi solver: every move, the call count and the time.
 */
public class HanoiResult {
    public final HanoiMove[] moves; // all moves, in order
    public final long operations; // recursive calls counted
    public final long nanos; // time taken

    public HanoiResult(HanoiMove[] moves, long operations, long nanos) {
        this.moves = moves;
        this.operations = operations;
        this.nanos = nanos;
    }
}