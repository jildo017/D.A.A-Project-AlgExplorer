package algexplorer.sorting;

import algexplorer.core.StopWatch;

/**
 * Merge sort (top-down). Best, average and worst O(n log n). Extra space O(n).
 * Stable.
 * Comparisons: each time a value from the left half is compared with one from
 * the right half.
 * Moves: every write into the array (each value is written to a helper array,
 * then copied back).
 */
public class MergeSort implements Sorter {
    public String name() {
        return "Merge sort";
    }

    public SortResult sort(int[] input, boolean recordSteps) {
        int[] a = ArrayTools.copy(input);
        int n = a.length;
        StepLog log = recordSteps ? new StepLog() : null;
        Run run = new Run(a, log);
        if (log != null) {
            log.add("Start: the unsorted array", a, -1, -1, -1, -1);
        }

        StopWatch watch = new StopWatch();
        watch.start();
        if (n > 1) {
            run.split(0, n - 1);
        }
        watch.stop();

        return new SortResult(name(), a, run.comparisons, run.moves, watch.getNanos(),
                log == null ? new SortStep[0] : log.toArray(), log != null && log.wasCut());
    }

    /**
     * Holds everything one sort needs, so that a MergeSort object can be used again
     * and again.
     */
    private static class Run {
        private final int[] a; // the array being sorted
        private final int[] helper; // a work array of the same size
        private final StepLog log; // null when steps are off
        long comparisons = 0;
        long moves = 0;

        Run(int[] a, StepLog log) {
            this.a = a;
            this.helper = new int[a.length];
            this.log = log;
        }

        /**
         * Sorts positions lo to hi: sort the left half, sort the right half, then merge
         * them.
         */
        void split(int lo, int hi) {
            if (lo >= hi) { // zero or one value: already sorted
                return;
            }
            int mid = lo + (hi - lo) / 2;
            split(lo, mid);
            split(mid + 1, hi);
            merge(lo, mid, hi);
        }

        /**
         * Merges the sorted runs lo..mid and mid+1..hi into one sorted run. Time: O(hi
         * - lo).
         */
        private void merge(int lo, int mid, int hi) {
            int i = lo; // finger on the left run
            int j = mid + 1; // finger on the right run
            int k = lo; // where the next value goes in the helper
            while (i <= mid && j <= hi) {
                comparisons++;
                if (a[i] <= a[j]) { // "<=" takes the left value on a tie, which keeps the sort stable
                    helper[k] = a[i];
                    i++;
                } else {
                    helper[k] = a[j];
                    j++;
                }
                k++;
                moves++;
            }
            while (i <= mid) { // the right run ran out: copy what is left of the left run
                helper[k] = a[i];
                i++;
                k++;
                moves++;
            }
            while (j <= hi) { // the left run ran out: copy what is left of the right run
                helper[k] = a[j];
                j++;
                k++;
                moves++;
            }
            for (int x = lo; x <= hi; x++) { // copy the merged run back into the main array
                a[x] = helper[x];
                moves++;
            }
            if (log != null) {
                log.add("Merged positions " + lo + " to " + hi
                        + ": two sorted runs became one sorted run", a, -1, -1, lo, hi);
            }
        }
    }
}