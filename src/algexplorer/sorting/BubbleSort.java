package algexplorer.sorting;

import algexplorer.core.StopWatch;

/**
 * Bubble sort with an early stop. Best O(n) (already sorted), average and worst
 * O(n^2). Extra space O(1). Stable.
 */
public class BubbleSort implements Sorter {
    public String name() {
        return "Bubble sort";
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
        for (int pass = 1; pass < n; pass++) {
            boolean swapped = false;
            int lastA = -1;
            int lastB = -1;
            for (int j = 0; j < n - pass; j++) { // the last (pass - 1) values are already in place
                comparisons++;
                if (a[j] > a[j + 1]) {
                    int temp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = temp;
                    swaps++;
                    swapped = true;
                    lastA = j;
                    lastB = j + 1;
                }
            }
            if (log != null) {
                log.add(swapped
                        ? "Pass " + pass + ": the largest unsorted value bubbled up to position " + (n - pass)
                        : "Pass " + pass + ": no swaps at all, so the array is sorted and we stop early",
                        a, lastA, lastB, n - pass, n - 1);
            }
            if (!swapped) {
                break;
            }
        }
        watch.stop();

        return new SortResult(name(), a, comparisons, swaps, watch.getNanos(),
                log == null ? new SortStep[0] : log.toArray(), log != null && log.wasCut());
    }
}