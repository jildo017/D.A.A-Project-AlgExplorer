package algorithms;

import datastructures.CustomLinkedList;
import utils.ExecutionTracker;

/** All 10 sorting algorithms, written from scratch on raw int[] arrays. */
public class SortingAlgorithms {
    public static final String[] NAMES = {
        "Bubble Sort", "Selection Sort", "Insertion Sort", "Shell Sort", "Merge Sort",
        "Quick Sort", "Heap Sort", "Counting Sort", "Radix Sort", "Bucket Sort", "Pancake Sort"
    };

    private SortingAlgorithms() {
    }

    /** Runs the algorithm with the given display name. */
    public static void run(String name, int[] arr, ExecutionTracker t, StepListener l) {
        switch (name) {
            case "Bubble Sort": bubbleSort(arr, t, l); break;
            case "Selection Sort": selectionSort(arr, t, l); break;
            case "Insertion Sort": insertionSort(arr, t, l); break;
            case "Shell Sort": shellSort(arr, t, l); break;
            case "Merge Sort": mergeSort(arr, t, l); break;
            case "Quick Sort": quickSort(arr, t, l); break;
            case "Heap Sort": heapSort(arr, t, l); break;
            case "Counting Sort": countingSort(arr, t, l); break;
            case "Radix Sort": radixSort(arr, t, l); break;
            case "Bucket Sort": bucketSort(arr, t, l); break;
            case "Pancake Sort": pancakeSort(arr, t, l); break;
            default: throw new IllegalArgumentException("Unknown sorting algorithm: " + name);
        }
    }

    // ------------------------------------------------------------------ helpers

    /** Encodes "indices from start to the end are sorted" for StepListener. */
    private static int suffix(int start) {
        return -(start + 2);
    }

    private static boolean on(ExecutionTracker t, StepListener l) {
        return l != null || t.isLogging();
    }

    private static void step(int[] a, int h1, int h2, int boundary, String msg, ExecutionTracker t, StepListener l) {
        if (t.isLogging()) {
            t.addStep(msg);
        }
        if (l != null) {
            l.onStep(a, h1, h2, boundary, msg);
        }
    }

    private static void cmp(int[] a, int i, int j, int boundary, ExecutionTracker t, StepListener l) {
        t.incrementOperations();
        if (on(t, l)) {
            step(a, i, j, boundary, "Compare arr[" + i + "]=" + a[i] + " with arr[" + j + "]=" + a[j], t, l);
        }
    }

    private static void swap(int[] a, int i, int j, int boundary, ExecutionTracker t, StepListener l) {
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
        t.incrementOperations();
        if (on(t, l)) {
            step(a, i, j, boundary, "Swap arr[" + i + "] and arr[" + j + "] -> " + a[i] + " and " + a[j], t, l);
        }
    }

    private static void finish(int[] a, ExecutionTracker t, StepListener l) {
        if (on(t, l)) {
            step(a, -1, -1, a.length, "Sorting complete", t, l);
        }
    }

    private static void checkNonNegative(int[] a, String name) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] < 0) {
                throw new IllegalArgumentException(name + " needs all numbers >= 0 (found " + a[i] + " at index " + i + ")");
            }
        }
    }

    private static int maxOf(int[] a, ExecutionTracker t) {
        int max = a[0];
        for (int i = 1; i < a.length; i++) {
            t.incrementOperations();
            if (a[i] > max) {
                max = a[i];
            }
        }
        return max;
    }

    // ------------------------------------------------------------------ 1. bubble

    public static void bubbleSort(int[] arr, ExecutionTracker t, StepListener l) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                cmp(arr, j, j + 1, suffix(n - i), t, l);
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1, suffix(n - i), t, l);
                    swapped = true;
                }
            }
            if (on(t, l)) {
                step(arr, -1, -1, suffix(n - 1 - i), "Pass " + (i + 1) + " done: arr[" + (n - 1 - i) + "] is in its final place", t, l);
            }
            if (!swapped) {
                if (on(t, l)) {
                    step(arr, -1, -1, -1, "No swaps in this pass, so the array is already sorted", t, l);
                }
                break;
            }
        }
        finish(arr, t, l);
    }

    // ------------------------------------------------------------------ 2. selection

    public static void selectionSort(int[] arr, ExecutionTracker t, StepListener l) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                cmp(arr, j, min, i, t, l);
                if (arr[j] < arr[min]) {
                    min = j;
                }
            }
            if (min != i) {
                swap(arr, i, min, i, t, l);
            }
            if (on(t, l)) {
                step(arr, -1, -1, i + 1, "arr[" + i + "]=" + arr[i] + " is now in its final place", t, l);
            }
        }
        finish(arr, t, l);
    }

    // ------------------------------------------------------------------ 3. insertion

    public static void insertionSort(int[] arr, ExecutionTracker t, StepListener l) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;
            if (on(t, l)) {
                step(arr, i, -1, -1, "Pick key = " + key + " (index " + i + ")", t, l);
            }
            while (j >= 0) {
                t.incrementOperations();
                if (on(t, l)) {
                    step(arr, j, j + 1, -1, "Compare key " + key + " with arr[" + j + "]=" + arr[j], t, l);
                }
                if (arr[j] > key) {
                    arr[j + 1] = arr[j];
                    t.incrementOperations();
                    if (on(t, l)) {
                        step(arr, j + 1, j, -1, "Shift arr[" + j + "]=" + arr[j] + " one place right", t, l);
                    }
                    j--;
                } else {
                    break;
                }
            }
            arr[j + 1] = key;
            if (on(t, l)) {
                step(arr, j + 1, -1, -1, "Insert key " + key + " at index " + (j + 1), t, l);
            }
        }
        finish(arr, t, l);
    }

    // ------------------------------------------------------------------ 4. shell

    public static void shellSort(int[] arr, ExecutionTracker t, StepListener l) {
        int n = arr.length;
        for (int gap = n / 2; gap > 0; gap /= 2) {
            if (on(t, l)) {
                step(arr, -1, -1, -1, "New gap = " + gap, t, l);
            }
            for (int i = gap; i < n; i++) {
                int temp = arr[i];
                int j = i;
                while (j >= gap) {
                    t.incrementOperations();
                    if (on(t, l)) {
                        step(arr, j - gap, j, -1, "Compare arr[" + (j - gap) + "]=" + arr[j - gap] + " with temp=" + temp + " (gap " + gap + ")", t, l);
                    }
                    if (arr[j - gap] > temp) {
                        arr[j] = arr[j - gap];
                        t.incrementOperations();
                        if (on(t, l)) {
                            step(arr, j, j - gap, -1, "Shift arr[" + (j - gap) + "] to index " + j, t, l);
                        }
                        j -= gap;
                    } else {
                        break;
                    }
                }
                if (j != i) {
                    arr[j] = temp;
                    t.incrementOperations();
                    if (on(t, l)) {
                        step(arr, j, -1, -1, "Place temp=" + temp + " at index " + j, t, l);
                    }
                }
            }
        }
        finish(arr, t, l);
    }

    // ------------------------------------------------------------------ 5. merge

    public static void mergeSort(int[] arr, ExecutionTracker t, StepListener l) {
        if (arr.length > 1) {
            mergeSortRec(arr, 0, arr.length - 1, t, l);
        }
        finish(arr, t, l);
    }

    private static void mergeSortRec(int[] a, int lo, int hi, ExecutionTracker t, StepListener l) {
        t.incrementOperations();
        if (lo >= hi) {
            return;
        }
        int mid = (lo + hi) / 2;
        if (on(t, l)) {
            step(a, lo, hi, -1, "Split [" + lo + ".." + hi + "] into [" + lo + ".." + mid + "] and [" + (mid + 1) + ".." + hi + "]", t, l);
        }
        mergeSortRec(a, lo, mid, t, l);
        mergeSortRec(a, mid + 1, hi, t, l);
        merge(a, lo, mid, hi, t, l);
    }

    private static void merge(int[] a, int lo, int mid, int hi, ExecutionTracker t, StepListener l) {
        int n1 = mid - lo + 1;
        int n2 = hi - mid;
        int[] left = new int[n1];
        int[] right = new int[n2];
        for (int x = 0; x < n1; x++) {
            left[x] = a[lo + x];
        }
        for (int y = 0; y < n2; y++) {
            right[y] = a[mid + 1 + y];
        }
        if (on(t, l)) {
            step(a, lo, hi, -1, "Merge left[" + lo + ".." + mid + "] and right[" + (mid + 1) + ".." + hi + "]", t, l);
        }
        int i = 0;
        int j = 0;
        int k = lo;
        while (i < n1 && j < n2) {
            t.incrementOperations();
            if (left[i] <= right[j]) {
                a[k] = left[i];
                if (on(t, l)) {
                    step(a, k, -1, -1, "left[" + i + "]=" + left[i] + " <= right[" + j + "]=" + right[j] + ": place " + left[i] + " at index " + k, t, l);
                }
                i++;
            } else {
                a[k] = right[j];
                if (on(t, l)) {
                    step(a, k, -1, -1, "left[" + i + "]=" + left[i] + " > right[" + j + "]=" + right[j] + ": place " + right[j] + " at index " + k, t, l);
                }
                j++;
            }
            t.incrementOperations();
            k++;
        }
        while (i < n1) {
            a[k] = left[i];
            t.incrementOperations();
            if (on(t, l)) {
                step(a, k, -1, -1, "Copy leftover left[" + i + "]=" + left[i] + " to index " + k, t, l);
            }
            i++;
            k++;
        }
        while (j < n2) {
            a[k] = right[j];
            t.incrementOperations();
            if (on(t, l)) {
                step(a, k, -1, -1, "Copy leftover right[" + j + "]=" + right[j] + " to index " + k, t, l);
            }
            j++;
            k++;
        }
    }

    // ------------------------------------------------------------------ 6. quick (Lomuto)

    public static void quickSort(int[] arr, ExecutionTracker t, StepListener l) {
        quickSortRec(arr, 0, arr.length - 1, t, l);
        finish(arr, t, l);
    }

    private static void quickSortRec(int[] a, int lo, int hi, ExecutionTracker t, StepListener l) {
        t.incrementOperations();
        if (lo >= hi) {
            return;
        }
        int p = partition(a, lo, hi, t, l);
        quickSortRec(a, lo, p - 1, t, l);
        quickSortRec(a, p + 1, hi, t, l);
    }

    private static int partition(int[] a, int lo, int hi, ExecutionTracker t, StepListener l) {
        int pivot = a[hi];
        if (on(t, l)) {
            step(a, hi, -1, -1, "Partition [" + lo + ".." + hi + "] with pivot arr[" + hi + "]=" + pivot, t, l);
        }
        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            cmp(a, j, hi, -1, t, l);
            if (a[j] <= pivot) {
                i++;
                if (i != j) {
                    swap(a, i, j, -1, t, l);
                }
            }
        }
        if (i + 1 != hi) {
            swap(a, i + 1, hi, -1, t, l);
        }
        if (on(t, l)) {
            step(a, i + 1, -1, -1, "Pivot " + pivot + " is now at its final index " + (i + 1), t, l);
        }
        return i + 1;
    }

    // ------------------------------------------------------------------ 7. heap

    public static void heapSort(int[] arr, ExecutionTracker t, StepListener l) {
        int n = arr.length;
        if (on(t, l)) {
            step(arr, -1, -1, -1, "Build a max-heap (left = 2i+1, right = 2i+2)", t, l);
        }
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i, -1, t, l);
        }
        for (int end = n - 1; end > 0; end--) {
            swap(arr, 0, end, suffix(end + 1), t, l);
            if (on(t, l)) {
                step(arr, -1, -1, suffix(end), "Largest value " + arr[end] + " moved to its final index " + end, t, l);
            }
            heapify(arr, end, 0, suffix(end), t, l);
        }
        finish(arr, t, l);
    }

    private static void heapify(int[] a, int size, int i, int boundary, ExecutionTracker t, StepListener l) {
        while (true) {
            int largest = i;
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            if (left < size) {
                cmp(a, left, largest, boundary, t, l);
                if (a[left] > a[largest]) {
                    largest = left;
                }
            }
            if (right < size) {
                cmp(a, right, largest, boundary, t, l);
                if (a[right] > a[largest]) {
                    largest = right;
                }
            }
            if (largest == i) {
                break;
            }
            swap(a, i, largest, boundary, t, l);
            i = largest;
        }
    }

    // ------------------------------------------------------------------ 8. counting

    public static void countingSort(int[] arr, ExecutionTracker t, StepListener l) {
        checkNonNegative(arr, "Counting Sort");
        int n = arr.length;
        if (n == 0) {
            return;
        }
        int max = maxOf(arr, t);
        if (max > 1000000) {
            throw new IllegalArgumentException("Counting Sort: largest value " + max + " is too big (limit 1,000,000)");
        }
        int[] count = new int[max + 1];
        int[] output = new int[n];
        for (int i = 0; i < n; i++) {
            count[arr[i]]++;
            t.incrementOperations();
            if (on(t, l)) {
                step(arr, i, -1, -1, "Count value " + arr[i] + ": count[" + arr[i] + "] = " + count[arr[i]], t, l);
            }
        }
        for (int v = 1; v <= max; v++) {
            count[v] += count[v - 1];
            t.incrementOperations();
        }
        if (on(t, l)) {
            step(arr, -1, -1, -1, "Prefix sums computed: count[v] = number of values <= v", t, l);
        }
        for (int i = n - 1; i >= 0; i--) {
            int v = arr[i];
            count[v]--;
            output[count[v]] = v;
            t.incrementOperations();
            if (on(t, l)) {
                step(arr, i, count[v], -1, "Place " + v + " at output position " + count[v], t, l);
            }
        }
        for (int i = 0; i < n; i++) {
            arr[i] = output[i];
            t.incrementOperations();
            if (on(t, l)) {
                step(arr, i, -1, i + 1, "Copy output[" + i + "]=" + output[i] + " back into arr[" + i + "]", t, l);
            }
        }
        finish(arr, t, l);
    }

    // ------------------------------------------------------------------ 9. radix

    public static void radixSort(int[] arr, ExecutionTracker t, StepListener l) {
        checkNonNegative(arr, "Radix Sort");
        int n = arr.length;
        if (n == 0) {
            return;
        }
        int max = maxOf(arr, t);
        int[] output = new int[n];
        for (long exp = 1; max / exp > 0; exp *= 10) {
            int[] count = new int[10];
            if (on(t, l)) {
                step(arr, -1, -1, -1, "Pass: sort by the digit at position " + exp + " (units, tens, hundreds...)", t, l);
            }
            for (int i = 0; i < n; i++) {
                int d = (int) ((arr[i] / exp) % 10);
                count[d]++;
                t.incrementOperations();
                if (on(t, l)) {
                    step(arr, i, -1, -1, "Digit of " + arr[i] + " is " + d, t, l);
                }
            }
            for (int d = 1; d < 10; d++) {
                count[d] += count[d - 1];
                t.incrementOperations();
            }
            for (int i = n - 1; i >= 0; i--) {
                int d = (int) ((arr[i] / exp) % 10);
                count[d]--;
                output[count[d]] = arr[i];
                t.incrementOperations();
            }
            for (int i = 0; i < n; i++) {
                arr[i] = output[i];
                t.incrementOperations();
                if (on(t, l)) {
                    step(arr, i, -1, -1, "After this pass arr[" + i + "] = " + arr[i], t, l);
                }
            }
        }
        finish(arr, t, l);
    }

    // ------------------------------------------------------------------ 10. bucket

    public static void bucketSort(int[] arr, ExecutionTracker t, StepListener l) {
        checkNonNegative(arr, "Bucket Sort");
        int n = arr.length;
        if (n == 0) {
            return;
        }
        int max = maxOf(arr, t);
        CustomLinkedList[] buckets = new CustomLinkedList[n];
        for (int i = 0; i < n; i++) {
            buckets[i] = new CustomLinkedList();
        }
        for (int i = 0; i < n; i++) {
            int idx = (int) (((long) arr[i] * n) / ((long) max + 1));
            if (on(t, l)) {
                step(arr, i, -1, -1, "Value " + arr[i] + " goes into bucket " + idx, t, l);
            }
            buckets[idx].insertEnd(arr[i], t);
        }
        int pos = 0;
        for (int b = 0; b < n; b++) {
            int[] values = buckets[b].toArray();
            if (values.length == 0) {
                continue;
            }
            insertionSortPlain(values, t);
            if (on(t, l)) {
                step(arr, -1, -1, -1, "Bucket " + b + " sorted with insertion sort", t, l);
            }
            for (int k = 0; k < values.length; k++) {
                arr[pos] = values[k];
                t.incrementOperations();
                if (on(t, l)) {
                    step(arr, pos, -1, pos + 1, "Copy " + values[k] + " from bucket " + b + " to arr[" + pos + "]", t, l);
                }
                pos++;
            }
        }
        finish(arr, t, l);
    }

    private static void insertionSortPlain(int[] a, ExecutionTracker t) {
        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= 0) {
                t.incrementOperations();
                if (a[j] > key) {
                    a[j + 1] = a[j];
                    t.incrementOperations();
                    j--;
                } else {
                    break;
                }
            }
            a[j + 1] = key;
        }
    }

    // ------------------------------------------------------------------ 11. pancake sort

    public static void pancakeSort(int[] arr, ExecutionTracker t, StepListener l) {
        int n = arr.length;
        for (int currSize = n; currSize > 1; currSize--) {
            int maxIdx = 0;
            for (int i = 1; i < currSize; i++) {
                cmp(arr, i, maxIdx, currSize, t, l);
                if (arr[i] > arr[maxIdx]) {
                    maxIdx = i;
                }
            }
            if (maxIdx != currSize - 1) {
                if (maxIdx != 0) {
                    if (on(t, l)) {
                        step(arr, 0, maxIdx, currSize, "Flip prefix [0.." + maxIdx + "] to bring maximum value " + arr[maxIdx] + " to front", t, l);
                    }
                    flip(arr, maxIdx, currSize, t, l);
                }
                if (on(t, l)) {
                    step(arr, 0, currSize - 1, currSize, "Flip prefix [0.." + (currSize - 1) + "] to place maximum value at index " + (currSize - 1), t, l);
                }
                flip(arr, currSize - 1, currSize, t, l);
            }
        }
        finish(arr, t, l);
    }

    private static void flip(int[] a, int k, int boundary, ExecutionTracker t, StepListener l) {
        int start = 0;
        while (start < k) {
            swap(a, start, k, boundary, t, l);
            start++;
            k--;
        }
    }
}
