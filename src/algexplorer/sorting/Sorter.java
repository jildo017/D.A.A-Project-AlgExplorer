package algexplorer.sorting;

/**
 * What every sorting algorithm can do. The input array is never changed: a
 * sorted copy is returned.
 */
public interface Sorter {
    String name();

    /**
     * Sorts a copy of input. With recordSteps false no steps are saved (use that
     * when timing).
     */
    SortResult sort(int[] input, boolean recordSteps);
}