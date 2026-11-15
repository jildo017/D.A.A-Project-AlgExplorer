package algexplorer.sorting;

import algexplorer.core.StopWatch;

/**
 * Insertion sort. Best O(n) (already sorted), average and worst O(n^2). Extra
 * space O(1). Stable.
 */
public class InsertionSort implements Sorter {
    public String name() {
        return "Insertion sort";
    }

    public SortResult sort(int[] input, boolean recordSteps) {
        int[] a = ArrayTools.copy(input);
        int n = a.length;
        StepLog log = recordSteps ? new StepLog() : null;
        long comparisons = 0;
        long moves = 0;
        if (log != null) {
            log.add("Start: the unsorted array", a, -1, -1, -1, -1);
        }

        StopWatch watch = new StopWatch();
        watch.start();
        for (int i = 1; i < n; i++) {
            int key = a[i]; // the value we are going to insert
            int j = i - 1;
            while (j >= 0) {
                comparisons++;
                if (a[j] > key) {
                    a[j + 1] = a[j]; // shift the bigger value one place right
                    moves++;
                    j--;
                } else {
                    break;
                }
            }
            if (j + 1 != i) { // the key really moved: write it into the gap
                a[j + 1] = key;
                moves++;
            }
            if (log != null) {
                log.add("Pass " + i + ": " + key + " was inserted into the sorted part, now at position "
                        + (j + 1), a, j + 1, -1, 0, i);
            }
        }
        watch.stop();

        return new SortResult(name(), a, comparisons, moves, watch.getNanos(),
                log == null ? new SortStep[0] : log.toArray(), log != null && log.wasCut());
    }
}