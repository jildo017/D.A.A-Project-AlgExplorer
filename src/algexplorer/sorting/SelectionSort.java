package algexplorer.sorting;

import algexplorer.core.StopWatch;

/**
 * Selection sort. Always O(n^2) comparisons, but at most n - 1 swaps. Extra
 * space O(1). Not stable.
 */
public class SelectionSort implements Sorter {
    public String name() {
        return "Selection sort";
    }

    public SortResult sort(int[] input, boolean recordSteps) {
        int[] a = ArrayTools.copy(input);
        int n = a.length;
        StepLog log = recordSteps ? new StepLog() : null;
        long comparisons = 0;
        long swaps = 0;
        if (log != null) {
            log.add("Start: the unsorted array", a, -1, -1, -1, -1);
        }

        StopWatch watch = new StopWatch();
        watch.start();
        for (int i = 0; i < n - 1; i++) {
            int min = i; // position of the smallest value seen so far
            for (int j = i + 1; j < n; j++) {
                comparisons++;
                if (a[j] < a[min]) {
                    min = j;
                }
            }
            boolean swapped = min != i;
            if (swapped) {
                int temp = a[i];
                a[i] = a[min];
                a[min] = temp;
                swaps++;
            }
            if (log != null) {
                log.add("Pass " + (i + 1) + ": the smallest unsorted value (" + a[i] + ") "
                        + (swapped ? "was swapped into position " + i : "was already in position " + i),
                        a, i, swapped ? min : -1, 0, i);
            }
        }
        watch.stop();

        return new SortResult(name(), a, comparisons, swaps, watch.getNanos(),
                log == null ? new SortStep[0] : log.toArray(), log != null && log.wasCut());
    }
}