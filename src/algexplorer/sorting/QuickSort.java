package algexplorer.sorting;

import algexplorer.core.StopWatch;
import java.util.Random;

/**
 * Quicksort. The pivot is the first value of the range (or a randomly chosen
 * value in the randomised version).
 * Partition: green = values smaller than the pivot, yellow = values greater
 * than or equal to it.
 * Best and average O(n log n); worst O(n^2) (first-element pivot on sorted
 * input). Extra space O(log n). Not stable.
 * Comparisons: each time a value is compared with the pivot. Moves: swaps.
 */
public class QuickSort implements Sorter {
    private final boolean randomPivot;
    private final Random random;

    /** randomPivot false = first-element pivot, true = randomly chosen pivot. */
    public QuickSort(boolean randomPivot) {
        this(randomPivot, System.nanoTime());
    }

    /**
     * The same, with a fixed seed so that a random run can be repeated exactly
     * (used for testing).
     */
    public QuickSort(boolean randomPivot, long seed) {
        this.randomPivot = randomPivot;
        this.random = new Random(seed);
    }

    public String name() {
        return randomPivot ? "Quicksort (random pivot)" : "Quicksort (first pivot)";
    }

    public SortResult sort(int[] input, boolean recordSteps) {
        int[] a = ArrayTools.copy(input);
        int n = a.length;
        StepLog log = recordSteps ? new StepLog() : null;
        Run run = new Run(a, log, randomPivot, random);
        if (log != null) {
            log.add("Start: the unsorted array", a, -1, -1, -1, -1);
        }

        StopWatch watch = new StopWatch();
        watch.start();
        run.sortRange(0, n - 1);
        watch.stop();

        return new SortResult(name(), a, run.comparisons, run.moves, watch.getNanos(),
                log == null ? new SortStep[0] : log.toArray(), log != null && log.wasCut());
    }

    /**
     * Holds everything one sort needs, so that a QuickSort object can be used again
     * and again.
     */
    private static class Run {
        private final int[] a;
        private final StepLog log;
        private final boolean randomPivot;
        private final Random random;
        long comparisons = 0;
        long moves = 0;

        Run(int[] a, StepLog log, boolean randomPivot, Random random) {
            this.a = a;
            this.log = log;
            this.randomPivot = randomPivot;
            this.random = random;
        }

        /**
         * Sorts positions lo to hi. We call ourselves on the SMALLER side and loop on
         * the larger side, so the
         * recursion is never deeper than about log2(n), even when the partitions are
         * very uneven.
         */
        void sortRange(int lo, int hi) {
            while (lo < hi) {
                int p = partition(lo, hi); // the pivot is now in its final place p
                if (p - lo < hi - p) {
                    sortRange(lo, p - 1);
                    lo = p + 1;
                } else {
                    sortRange(p + 1, hi);
                    hi = p - 1;
                }
            }
        }

        private void swap(int x, int y) {
            int temp = a[x];
            a[x] = a[y];
            a[y] = temp;
            moves++;
        }

        /**
         * Partitions lo..hi around the pivot and returns the pivot's final position.
         * Time: O(hi - lo).
         */
        private int partition(int lo, int hi) {
            if (randomPivot) {
                int chosen = lo + random.nextInt(hi - lo + 1);
                if (chosen != lo) {
                    swap(lo, chosen); // bring the random pivot to the front
                }
            }
            int pivot = a[lo];
            int green = lo; // the last position of the green part (smaller than pivot)
            for (int j = lo + 1; j <= hi; j++) { // positions green+1 .. j-1 are yellow (>= pivot)
                comparisons++;
                if (a[j] < pivot) {
                    green++; // the green part grows by one place...
                    if (green != j) {
                        swap(green, j); // ...and the small value swaps with the first yellow one
                    }
                }
            }
            if (green != lo) {
                swap(lo, green); // finally the pivot goes between green and yellow
            }
            if (log != null) {
                log.add("Partition positions " + lo + " to " + hi + " around pivot " + pivot
                        + (randomPivot ? " (chosen at random)" : " (the first value)")
                        + ": it is now in its final position " + green,
                        a, green, -1, lo, hi);
            }
            return green;
        }
    }
}