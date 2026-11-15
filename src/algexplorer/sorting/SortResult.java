package algexplorer.sorting;

/**
 * The answer of a sort: the sorted array, the counts, the time and (optionally)
 * every step.
 */
public class SortResult {
    public final String algorithm;
    public final int[] sorted;
    public final long comparisons;
    public final long moves; // swaps for bubble, selection and quicksort; array writes for insertion and
                             // merge
    public final long nanos;
    public final SortStep[] steps; // empty if steps were not requested
    public final boolean stepsCut;

    public SortResult(String algorithm, int[] sorted, long comparisons, long moves, long nanos,
            SortStep[] steps, boolean stepsCut) {
        this.algorithm = algorithm;
        this.sorted = sorted;
        this.comparisons = comparisons;
        this.moves = moves;
        this.nanos = nanos;
        this.steps = steps;
        this.stepsCut = stepsCut;
    }
}