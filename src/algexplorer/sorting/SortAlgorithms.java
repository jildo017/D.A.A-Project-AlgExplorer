package algexplorer.sorting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SortAlgorithms {

    public static class SortStep {
        public final int[] array;
        public final int indexA;        // Primary active index (e.g. current element)
        public final int indexB;        // Secondary active index (e.g. compared element or target)
        public final int specialIndex;  // Pivot, minimum, or barrier index
        public final boolean isSwap;    // True if array was modified at this step
        public final String description;
        public final int comparisons;
        public final int swaps;

        public SortStep(int[] array, int indexA, int indexB, int specialIndex, boolean isSwap,
                        String description, int comparisons, int swaps) {
            this.array = Arrays.copyOf(array, array.length);
            this.indexA = indexA;
            this.indexB = indexB;
            this.specialIndex = specialIndex;
            this.isSwap = isSwap;
            this.description = description;
            this.comparisons = comparisons;
            this.swaps = swaps;
        }
    }

    public static List<SortStep> getSteps(String algorithmName, int[] input) {
        int[] arr = Arrays.copyOf(input, input.length);
        return switch (algorithmName.toLowerCase().trim()) {
            case "bubble sort", "bubble" -> bubbleSortDetailed(arr);
            case "selection sort", "selection" -> selectionSortDetailed(arr);
            case "insertion sort", "insertion" -> insertionSortDetailed(arr);
            case "shell sort", "shell" -> shellSortDetailed(arr);
            case "merge sort", "merge" -> mergeSortDetailed(arr);
            case "quick sort", "quick" -> quickSortDetailed(arr);
            case "heap sort", "heap" -> heapSortDetailed(arr);
            case "counting sort", "counting" -> countingSortDetailed(arr);
            case "radix sort", "radix" -> radixSortDetailed(arr);
            case "bucket sort", "bucket" -> bucketSortDetailed(arr);
            case "cycle sort", "cycle" -> cycleSortDetailed(arr);
            case "tim sort", "tim" -> timSortDetailed(arr);
            case "pancake sort", "pancake" -> pancakeSortDetailed(arr);
            case "cocktail sort", "cocktail", "cocktail shaker" -> cocktailSortDetailed(arr);
            case "comb sort", "comb" -> combSortDetailed(arr);
            case "gnome sort", "gnome" -> gnomeSortDetailed(arr);
            default -> bubbleSortDetailed(arr);
        };
    }

    // 1. Bubble Sort
    public static List<SortStep> bubbleSortDetailed(int[] arr) {
        List<SortStep> steps = new ArrayList<>();
        int n = arr.length;
        int comps = 0, swaps = 0;
        steps.add(new SortStep(arr, -1, -1, -1, false, "Initial array. Bubble Sort compares adjacent pairs and bubbles the largest to the right.", comps, swaps));

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                comps++;
                boolean needSwap = arr[j] > arr[j + 1];
                String reason = needSwap
                        ? "Since " + arr[j] + " > " + arr[j + 1] + ", they are out of order -> Will swap!"
                        : "Since " + arr[j] + " <= " + arr[j + 1] + ", they are already sorted -> Keep position.";

                steps.add(new SortStep(arr, j, j + 1, n - i, false,
                        "Comparing arr[" + j + "] (" + arr[j] + ") and arr[" + (j + 1) + "] (" + arr[j + 1] + "). " + reason, comps, swaps));

                if (needSwap) {
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                    swaps++;
                    swapped = true;
                    steps.add(new SortStep(arr, j, j + 1, n - i, true,
                            "Swapped: Moved smaller " + arr[j] + " to index " + j + " and " + arr[j + 1] + " to index " + (j + 1) + ".", comps, swaps));
                }
            }
            if (!swapped) {
                steps.add(new SortStep(arr, -1, -1, -1, false, "Optimization: No swaps occurred in pass " + (i + 1) + ". Array is fully sorted early!", comps, swaps));
                break;
            }
        }
        steps.add(new SortStep(arr, -1, -1, -1, false, "Bubble Sort completed! Array is fully sorted.", comps, swaps));
        return steps;
    }

    // 2. Selection Sort
    public static List<SortStep> selectionSortDetailed(int[] arr) {
        List<SortStep> steps = new ArrayList<>();
        int n = arr.length;
        int comps = 0, swaps = 0;
        steps.add(new SortStep(arr, -1, -1, -1, false, "Initial array. Selection Sort finds the smallest element and places it into position i.", comps, swaps));

        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            steps.add(new SortStep(arr, i, -1, minIdx, false,
                    "Pass " + (i + 1) + ": Searching for minimum element in remaining range [" + i + ".." + (n - 1) + "]. Current min is arr[" + minIdx + "] = " + arr[minIdx] + ".", comps, swaps));

            for (int j = i + 1; j < n; j++) {
                comps++;
                if (arr[j] < arr[minIdx]) {
                    steps.add(new SortStep(arr, j, minIdx, i, false,
                            "Comparing arr[" + j + "] (" + arr[j] + ") < current min (" + arr[minIdx] + ") -> Found new minimum value " + arr[j] + " at index " + j + "!", comps, swaps));
                    minIdx = j;
                } else {
                    steps.add(new SortStep(arr, j, minIdx, i, false,
                            "Comparing arr[" + j + "] (" + arr[j] + ") >= current min (" + arr[minIdx] + ") -> Retaining current min.", comps, swaps));
                }
            }

            if (minIdx != i) {
                int tmp = arr[i];
                arr[i] = arr[minIdx];
                arr[minIdx] = tmp;
                swaps++;
                steps.add(new SortStep(arr, i, minIdx, -1, true,
                        "Swapped minimum element " + arr[i] + " into its final sorted position at index " + i + ".", comps, swaps));
            } else {
                steps.add(new SortStep(arr, i, -1, -1, false,
                        "Element " + arr[i] + " is already the minimum in this range. No swap needed for index " + i + ".", comps, swaps));
            }
        }
        steps.add(new SortStep(arr, -1, -1, -1, false, "Selection Sort completed! All elements sorted.", comps, swaps));
        return steps;
    }

    // 3. Insertion Sort
    public static List<SortStep> insertionSortDetailed(int[] arr) {
        List<SortStep> steps = new ArrayList<>();
        int n = arr.length;
        int comps = 0, swaps = 0;
        steps.add(new SortStep(arr, -1, -1, -1, false, "Initial array. Insertion Sort inserts each element into its correct position in the sorted prefix.", comps, swaps));

        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;
            steps.add(new SortStep(arr, i, -1, i, false,
                    "Selected key " + key + " at index " + i + " to insert into sorted prefix [0.." + (i - 1) + "].", comps, swaps));

            while (j >= 0) {
                comps++;
                if (arr[j] > key) {
                    steps.add(new SortStep(arr, j, j + 1, i, false,
                            "Since arr[" + j + "] (" + arr[j] + ") > key (" + key + "), shift " + arr[j] + " right to index " + (j + 1) + ".", comps, swaps));
                    arr[j + 1] = arr[j];
                    swaps++;
                    j--;
                } else {
                    steps.add(new SortStep(arr, j, -1, i, false,
                            "Found insertion spot: arr[" + j + "] (" + arr[j] + ") <= key (" + key + "). Stopping shifts.", comps, swaps));
                    break;
                }
            }
            arr[j + 1] = key;
            swaps++;
            steps.add(new SortStep(arr, j + 1, -1, -1, true,
                    "Inserted key " + key + " into correct sorted position at index " + (j + 1) + ".", comps, swaps));
        }
        steps.add(new SortStep(arr, -1, -1, -1, false, "Insertion Sort completed! Prefix fully expanded to entire array.", comps, swaps));
        return steps;
    }

    // 4. Shell Sort
    public static List<SortStep> shellSortDetailed(int[] arr) {
        List<SortStep> steps = new ArrayList<>();
        int n = arr.length;
        int comps = 0, swaps = 0;
        steps.add(new SortStep(arr, -1, -1, -1, false, "Initial array. Shell Sort performs gapped insertion sorts with decreasing gaps.", comps, swaps));

        for (int gap = n / 2; gap > 0; gap /= 2) {
            steps.add(new SortStep(arr, -1, -1, -1, false, "--- Starting gapped pass with GAP = " + gap + " ---", comps, swaps));
            for (int i = gap; i < n; i++) {
                int temp = arr[i];
                int j = i;
                while (j >= gap) {
                    comps++;
                    steps.add(new SortStep(arr, j, j - gap, -1, false,
                            "Comparing arr[" + (j - gap) + "] (" + arr[j - gap] + ") with arr[" + j + "] (" + temp + ") across gap " + gap + ".", comps, swaps));
                    if (arr[j - gap] > temp) {
                        arr[j] = arr[j - gap];
                        swaps++;
                        steps.add(new SortStep(arr, j, j - gap, -1, true,
                                "Shifted larger element " + arr[j] + " right by " + gap + " positions to index " + j + ".", comps, swaps));
                        j -= gap;
                    } else {
                        break;
                    }
                }
                arr[j] = temp;
                swaps++;
                steps.add(new SortStep(arr, j, -1, -1, true,
                        "Placed key " + temp + " into index " + j + " (gap " + gap + ").", comps, swaps));
            }
        }
        steps.add(new SortStep(arr, -1, -1, -1, false, "Shell Sort completed! Final gap-1 pass sorted the remaining items.", comps, swaps));
        return steps;
    }

    // 5. Merge Sort
    public static List<SortStep> mergeSortDetailed(int[] arr) {
        List<SortStep> steps = new ArrayList<>();
        int[] counters = new int[2];
        steps.add(new SortStep(arr, -1, -1, -1, false, "Initial array. Merge Sort recursively divides the array in half and merges sorted halves.", 0, 0));
        mergeSortHelper(arr, 0, arr.length - 1, steps, counters);
        steps.add(new SortStep(arr, -1, -1, -1, false, "Merge Sort completed! All sub-arrays merged into final sorted array.", counters[0], counters[1]));
        return steps;
    }

    private static void mergeSortHelper(int[] arr, int left, int right, List<SortStep> steps, int[] counters) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSortHelper(arr, left, mid, steps, counters);
            mergeSortHelper(arr, mid + 1, right, steps, counters);
            mergeDetailed(arr, left, mid, right, steps, counters);
        }
    }

    private static void mergeDetailed(int[] arr, int left, int mid, int right, List<SortStep> steps, int[] counters) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        int[] L = new int[n1];
        int[] R = new int[n2];
        for (int i = 0; i < n1; i++) L[i] = arr[left + i];
        for (int j = 0; j < n2; j++) R[j] = arr[mid + 1 + j];

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            counters[0]++;
            boolean chooseLeft = L[i] <= R[j];
            String choice = chooseLeft
                    ? "Left value " + L[i] + " <= Right value " + R[j] + " -> Placing " + L[i] + " at index " + k
                    : "Right value " + R[j] + " < Left value " + L[i] + " -> Placing " + R[j] + " at index " + k;

            steps.add(new SortStep(arr, left + i, mid + 1 + j, mid, false,
                    "Merge [" + left + ".." + right + "]: Comparing L[" + i + "]=" + L[i] + " vs R[" + j + "]=" + R[j] + ". " + choice, counters[0], counters[1]));

            if (chooseLeft) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            counters[1]++;
            steps.add(new SortStep(arr, k, -1, mid, true,
                    "Merged element " + arr[k] + " written to target position index " + k + ".", counters[0], counters[1]));
            k++;
        }
        while (i < n1) {
            arr[k] = L[i];
            counters[1]++;
            steps.add(new SortStep(arr, k, -1, mid, true,
                    "Copied remaining left element " + arr[k] + " to index " + k + ".", counters[0], counters[1]));
            i++;
            k++;
        }
        while (j < n2) {
            arr[k] = R[j];
            counters[1]++;
            steps.add(new SortStep(arr, k, -1, mid, true,
                    "Copied remaining right element " + arr[k] + " to index " + k + ".", counters[0], counters[1]));
            j++;
            k++;
        }
    }

    // 6. Quick Sort
    public static List<SortStep> quickSortDetailed(int[] arr) {
        List<SortStep> steps = new ArrayList<>();
        int[] counters = new int[2];
        steps.add(new SortStep(arr, -1, -1, -1, false, "Initial array. Quick Sort picks a pivot, partitions elements into smaller and larger, and recurses.", 0, 0));
        quickSortHelper(arr, 0, arr.length - 1, steps, counters);
        steps.add(new SortStep(arr, -1, -1, -1, false, "Quick Sort completed! Pivot partitions fully organized the array.", counters[0], counters[1]));
        return steps;
    }

    private static void quickSortHelper(int[] arr, int low, int high, List<SortStep> steps, int[] counters) {
        if (low < high) {
            int pi = partitionDetailed(arr, low, high, steps, counters);
            quickSortHelper(arr, low, pi - 1, steps, counters);
            quickSortHelper(arr, pi + 1, high, steps, counters);
        }
    }

    private static int partitionDetailed(int[] arr, int low, int high, List<SortStep> steps, int[] counters) {
        int pivot = arr[high];
        steps.add(new SortStep(arr, high, -1, high, false,
                "Partitioning range [" + low + ".." + high + "]. Selected PIVOT = " + pivot + " at index " + high + ".", counters[0], counters[1]));
        int i = low - 1;

        for (int j = low; j < high; j++) {
            counters[0]++;
            boolean smaller = arr[j] < pivot;
            String expl = smaller
                    ? "arr[" + j + "] (" + arr[j] + ") < pivot (" + pivot + ") -> Belongs on left partition."
                    : "arr[" + j + "] (" + arr[j] + ") >= pivot (" + pivot + ") -> Stays on right side.";

            steps.add(new SortStep(arr, j, high, high, false,
                    "Comparing arr[" + j + "] (" + arr[j] + ") with pivot (" + pivot + "). " + expl, counters[0], counters[1]));

            if (smaller) {
                i++;
                if (i != j) {
                    int tmp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = tmp;
                    counters[1]++;
                    steps.add(new SortStep(arr, i, j, high, true,
                            "Swapped arr[" + i + "] (" + arr[i] + ") and arr[" + j + "] (" + arr[j] + ") to group smaller element on left.", counters[0], counters[1]));
                }
            }
        }
        int tmp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = tmp;
        counters[1]++;
        steps.add(new SortStep(arr, i + 1, high, -1, true,
                "Placed pivot " + arr[i + 1] + " into its final resting partition index " + (i + 1) + ".", counters[0], counters[1]));
        return i + 1;
    }

    // 7. Heap Sort
    public static List<SortStep> heapSortDetailed(int[] arr) {
        List<SortStep> steps = new ArrayList<>();
        int n = arr.length;
        int[] counters = new int[2];
        steps.add(new SortStep(arr, -1, -1, -1, false, "Initial array. Heap Sort builds a Max-Heap where the root is always the largest element.", 0, 0));

        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i, steps, counters);
        }

        for (int i = n - 1; i > 0; i--) {
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;
            counters[1]++;
            steps.add(new SortStep(arr, 0, i, i, true,
                    "Extracted max element " + temp + " from heap root and placed at end index " + i + ".", counters[0], counters[1]));
            heapify(arr, i, 0, steps, counters);
        }
        steps.add(new SortStep(arr, -1, -1, -1, false, "Heap Sort completed! All max elements successively placed in sorted order.", counters[0], counters[1]));
        return steps;
    }

    private static void heapify(int[] arr, int n, int i, List<SortStep> steps, int[] counters) {
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;

        if (l < n) {
            counters[0]++;
            steps.add(new SortStep(arr, l, largest, -1, false,
                    "Heapify: Checking left child arr[" + l + "] (" + arr[l] + ") against root arr[" + largest + "] (" + arr[largest] + ").", counters[0], counters[1]));
            if (arr[l] > arr[largest]) largest = l;
        }

        if (r < n) {
            counters[0]++;
            steps.add(new SortStep(arr, r, largest, -1, false,
                    "Heapify: Checking right child arr[" + r + "] (" + arr[r] + ") against largest arr[" + largest + "] (" + arr[largest] + ").", counters[0], counters[1]));
            if (arr[r] > arr[largest]) largest = r;
        }

        if (largest != i) {
            int swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;
            counters[1]++;
            steps.add(new SortStep(arr, i, largest, -1, true,
                    "Swapped arr[" + i + "] with child arr[" + largest + "] (" + arr[i] + " < " + arr[largest] + ") to satisfy max-heap property.", counters[0], counters[1]));
            heapify(arr, n, largest, steps, counters);
        }
    }

    // 8. Counting Sort
    public static List<SortStep> countingSortDetailed(int[] arr) {
        List<SortStep> steps = new ArrayList<>();
        int n = arr.length;
        int comps = 0, moves = 0;
        steps.add(new SortStep(arr, -1, -1, -1, false, "Initial array. Counting Sort tallies frequencies of each value and writes them back in order.", comps, moves));
        if (n == 0) return steps;

        int min = arr[0], max = arr[0];
        for (int v : arr) {
            comps++;
            if (v < min) min = v;
            if (v > max) max = v;
        }

        int range = max - min + 1;
        int[] count = new int[range];
        for (int i = 0; i < n; i++) {
            count[arr[i] - min]++;
            steps.add(new SortStep(arr, i, -1, -1, false,
                    "Counting frequencies: Tallied occurrence of value " + arr[i] + " (Frequency now = " + count[arr[i] - min] + ").", comps, moves));
        }

        int idx = 0;
        for (int c = 0; c < range; c++) {
            while (count[c] > 0) {
                int val = c + min;
                arr[idx] = val;
                moves++;
                steps.add(new SortStep(arr, idx, -1, -1, true,
                        "Flushing frequency table: Placed value " + val + " into sorted position at index " + idx + ".", comps, moves));
                count[c]--;
                idx++;
            }
        }
        steps.add(new SortStep(arr, -1, -1, -1, false, "Counting Sort completed in O(n + k) non-comparative time!", comps, moves));
        return steps;
    }

    // 9. Radix Sort
    public static List<SortStep> radixSortDetailed(int[] arr) {
        List<SortStep> steps = new ArrayList<>();
        int n = arr.length;
        int comps = 0, moves = 0;
        steps.add(new SortStep(arr, -1, -1, -1, false, "Initial array. Radix Sort sorts numbers digit-by-digit from least significant to most significant.", comps, moves));
        if (n == 0) return steps;

        int min = arr[0];
        for (int v : arr) if (v < min) min = v;
        if (min < 0) {
            for (int i = 0; i < n; i++) arr[i] -= min;
        }

        int max = arr[0];
        for (int v : arr) if (v > max) max = v;

        for (int exp = 1; max / exp > 0; exp *= 10) {
            int[] output = new int[n];
            int[] count = new int[10];

            for (int i = 0; i < n; i++) {
                int digit = (arr[i] / exp) % 10;
                count[digit]++;
                steps.add(new SortStep(arr, i, -1, -1, false,
                        "Digit pass (10^" + (int) Math.log10(exp) + "): Value " + (arr[i] + (min < 0 ? min : 0)) + " has digit " + digit + " at position " + exp + ".", comps, moves));
            }

            for (int i = 1; i < 10; i++) count[i] += count[i - 1];

            for (int i = n - 1; i >= 0; i--) {
                int digit = (arr[i] / exp) % 10;
                output[count[digit] - 1] = arr[i];
                count[digit]--;
            }

            for (int i = 0; i < n; i++) {
                arr[i] = output[i];
                moves++;
                steps.add(new SortStep(arr, i, -1, -1, true,
                        "Placed " + (arr[i] + (min < 0 ? min : 0)) + " at index " + i + " based on digit pass " + exp + ".", comps, moves));
            }
        }

        if (min < 0) {
            for (int i = 0; i < n; i++) arr[i] += min;
        }

        steps.add(new SortStep(arr, -1, -1, -1, false, "Radix Sort completed! Multi-digit pass stably sorted all items.", comps, moves));
        return steps;
    }

    // 10. Bucket Sort
    public static List<SortStep> bucketSortDetailed(int[] arr) {
        List<SortStep> steps = new ArrayList<>();
        int n = arr.length;
        int comps = 0, moves = 0;
        steps.add(new SortStep(arr, -1, -1, -1, false, "Initial array. Bucket Sort partitions values into uniformly distributed buckets and sorts each.", comps, moves));
        if (n <= 1) return steps;

        int min = arr[0], max = arr[0];
        for (int v : arr) {
            if (v < min) min = v;
            if (v > max) max = v;
        }

        if (min == max) return steps;

        int bucketCount = Math.max(2, (int) Math.sqrt(n));
        List<List<Integer>> buckets = new ArrayList<>(bucketCount);
        for (int i = 0; i < bucketCount; i++) buckets.add(new ArrayList<>());

        for (int i = 0; i < n; i++) {
            int bIdx = (int) (((long) (arr[i] - min) * (bucketCount - 1)) / (max - min));
            buckets.get(bIdx).add(arr[i]);
            steps.add(new SortStep(arr, i, -1, bIdx, false,
                    "Assigned value " + arr[i] + " to Bucket #" + bIdx + ".", comps, moves));
        }

        int writeIdx = 0;
        for (int b = 0; b < bucketCount; b++) {
            List<Integer> bucket = buckets.get(b);
            for (int i = 1; i < bucket.size(); i++) {
                int key = bucket.get(i);
                int j = i - 1;
                while (j >= 0 && bucket.get(j) > key) {
                    comps++;
                    bucket.set(j + 1, bucket.get(j));
                    j--;
                }
                bucket.set(j + 1, key);
            }

            for (int val : bucket) {
                arr[writeIdx] = val;
                moves++;
                steps.add(new SortStep(arr, writeIdx, -1, b, true,
                        "Flushed sorted value " + val + " from Bucket #" + b + " into array index " + writeIdx + ".", comps, moves));
                writeIdx++;
            }
        }

        steps.add(new SortStep(arr, -1, -1, -1, false, "Bucket Sort completed! All buckets merged back into array.", comps, moves));
        return steps;
    }

    // 11. Cycle Sort
    public static List<SortStep> cycleSortDetailed(int[] arr) {
        List<SortStep> steps = new ArrayList<>();
        int n = arr.length;
        int comps = 0, writes = 0;
        steps.add(new SortStep(arr, -1, -1, -1, false, "Initial array. Cycle Sort is theoretically optimal: minimizes the number of memory writes.", comps, writes));

        for (int cycleStart = 0; cycleStart < n - 1; cycleStart++) {
            int item = arr[cycleStart];
            int pos = cycleStart;

            for (int i = cycleStart + 1; i < n; i++) {
                comps++;
                if (arr[i] < item) pos++;
            }

            if (pos == cycleStart) continue;

            while (item == arr[pos]) pos++;

            if (pos != cycleStart) {
                int temp = item;
                item = arr[pos];
                arr[pos] = temp;
                writes++;
                steps.add(new SortStep(arr, pos, cycleStart, -1, true,
                        "Cycle Start: Rotated value " + temp + " directly into its final calculated position index " + pos + ".", comps, writes));
            }

            while (pos != cycleStart) {
                pos = cycleStart;
                for (int i = cycleStart + 1; i < n; i++) {
                    comps++;
                    if (arr[i] < item) pos++;
                }

                while (item == arr[pos]) pos++;

                if (item != arr[pos]) {
                    int temp = item;
                    item = arr[pos];
                    arr[pos] = temp;
                    writes++;
                    steps.add(new SortStep(arr, pos, cycleStart, -1, true,
                            "Continuing Cycle: Placed displaced item " + temp + " into its correct slot index " + pos + ".", comps, writes));
                }
            }
        }

        steps.add(new SortStep(arr, -1, -1, -1, false, "Cycle Sort completed with minimal writes!", comps, writes));
        return steps;
    }

    // 12. Tim Sort
    public static List<SortStep> timSortDetailed(int[] arr) {
        List<SortStep> steps = new ArrayList<>();
        int n = arr.length;
        int[] counters = new int[2];
        final int RUN = 16;
        steps.add(new SortStep(arr, -1, -1, -1, false, "Initial array. Tim Sort divides data into small runs of " + RUN + ", sorts each with Insertion Sort, then merges.", 0, 0));

        for (int i = 0; i < n; i += RUN) {
            int right = Math.min(i + RUN - 1, n - 1);
            for (int k = i + 1; k <= right; k++) {
                int key = arr[k];
                int j = k - 1;
                while (j >= i && arr[j] > key) {
                    counters[0]++;
                    arr[j + 1] = arr[j];
                    counters[1]++;
                    steps.add(new SortStep(arr, j, j + 1, k, true,
                            "TimSort run insertion: Shifted " + arr[j] + " right to make room for key " + key + ".", counters[0], counters[1]));
                    j--;
                }
                arr[j + 1] = key;
                counters[1]++;
                steps.add(new SortStep(arr, j + 1, -1, -1, true,
                        "TimSort run insertion: Placed key " + key + " into position at index " + (j + 1) + ".", counters[0], counters[1]));
            }
        }

        for (int size = RUN; size < n; size = 2 * size) {
            for (int left = 0; left < n; left += 2 * size) {
                int mid = left + size - 1;
                int right = Math.min(left + 2 * size - 1, n - 1);
                if (mid < right) {
                    mergeDetailed(arr, left, mid, right, steps, counters);
                }
            }
        }

        steps.add(new SortStep(arr, -1, -1, -1, false, "Tim Sort completed! Hybrid runs stably merged.", counters[0], counters[1]));
        return steps;
    }

    // 13. Pancake Sort
    public static List<SortStep> pancakeSortDetailed(int[] arr) {
        List<SortStep> steps = new ArrayList<>();
        int n = arr.length;
        int comps = 0, flips = 0;
        steps.add(new SortStep(arr, -1, -1, -1, false,
                "Initial array. Pancake Sort only uses prefix reversals ('flips') to move the maximum element to the end.", comps, flips));

        for (int currSize = n; currSize > 1; currSize--) {
            int maxIdx = 0;
            steps.add(new SortStep(arr, 0, -1, currSize - 1, false,
                    "Pass for target position " + (currSize - 1) + ": Finding maximum in prefix [0.." + (currSize - 1) + "].", comps, flips));

            for (int i = 1; i < currSize; i++) {
                comps++;
                if (arr[i] > arr[maxIdx]) {
                    steps.add(new SortStep(arr, i, maxIdx, currSize - 1, false,
                            "arr[" + i + "] (" + arr[i] + ") > current max arr[" + maxIdx + "] (" + arr[maxIdx] + ") -> New maximum candidate!", comps, flips));
                    maxIdx = i;
                } else {
                    steps.add(new SortStep(arr, i, maxIdx, currSize - 1, false,
                            "arr[" + i + "] (" + arr[i] + ") <= current max arr[" + maxIdx + "] (" + arr[maxIdx] + ").", comps, flips));
                }
            }

            if (maxIdx != currSize - 1) {
                if (maxIdx > 0) {
                    flips++;
                    steps.add(new SortStep(arr, 0, maxIdx, currSize - 1, false,
                            "Spatula Insertion: Preparing to flip prefix [0.." + maxIdx + "] to bring max " + arr[maxIdx] + " to top (index 0).", comps, flips));
                    flip(arr, maxIdx);
                    steps.add(new SortStep(arr, 0, maxIdx, currSize - 1, true,
                            "🥞 Flip: Reversed prefix [0.." + maxIdx + "]. Maximum " + arr[0] + " is now at index 0!", comps, flips));
                }

                flips++;
                steps.add(new SortStep(arr, 0, currSize - 1, currSize - 1, false,
                        "Spatula Insertion: Preparing to flip prefix [0.." + (currSize - 1) + "] to place max " + arr[0] + " at target index " + (currSize - 1) + ".", comps, flips));
                flip(arr, currSize - 1);
                steps.add(new SortStep(arr, 0, currSize - 1, currSize - 1, true,
                        "🥞 Flip: Reversed prefix [0.." + (currSize - 1) + "]. Placed maximum " + arr[currSize - 1] + " at final sorted position index " + (currSize - 1) + "!", comps, flips));
            } else {
                steps.add(new SortStep(arr, currSize - 1, -1, currSize - 1, false,
                        "Maximum " + arr[currSize - 1] + " is already in position " + (currSize - 1) + ". No flip needed.", comps, flips));
            }
        }

        steps.add(new SortStep(arr, -1, -1, -1, false,
                "Pancake Sort completed! All elements sorted using " + flips + " spatula flips.", comps, flips));
        return steps;
    }

    private static void flip(int[] arr, int k) {
        int i = 0;
        while (i < k) {
            int tmp = arr[i];
            arr[i] = arr[k];
            arr[k] = tmp;
            i++;
            k--;
        }
    }

    // 14. Cocktail Shaker Sort
    public static List<SortStep> cocktailSortDetailed(int[] arr) {
        List<SortStep> steps = new ArrayList<>();
        int n = arr.length;
        int comps = 0, swaps = 0;
        steps.add(new SortStep(arr, -1, -1, -1, false,
                "Initial array. Cocktail Shaker Sort is bidirectional: bubbles max to the right, then min to the left.", comps, swaps));

        boolean swapped = true;
        int start = 0;
        int end = n - 1;

        while (swapped) {
            swapped = false;
            steps.add(new SortStep(arr, start, end, -1, false,
                    "➡️ Forward Pass: Bubbling largest element to index " + end + ".", comps, swaps));

            for (int i = start; i < end; i++) {
                comps++;
                boolean needSwap = arr[i] > arr[i + 1];
                String reason = needSwap
                        ? "arr[" + i + "] (" + arr[i] + ") > arr[" + (i + 1) + "] (" + arr[i + 1] + ") -> Out of order -> Swap!"
                        : "arr[" + i + "] (" + arr[i] + ") <= arr[" + (i + 1) + "] (" + arr[i + 1] + ") -> In order.";
                steps.add(new SortStep(arr, i, i + 1, end, false,
                        "Forward check: " + reason, comps, swaps));

                if (needSwap) {
                    int tmp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = tmp;
                    swaps++;
                    swapped = true;
                    steps.add(new SortStep(arr, i, i + 1, end, true,
                            "Swapped arr[" + i + "] and arr[" + (i + 1) + "].", comps, swaps));
                }
            }

            if (!swapped) break;
            swapped = false;
            end--;

            steps.add(new SortStep(arr, start, end, -1, false,
                    "⬅️ Backward Pass: Bubbling smallest element to index " + start + ".", comps, swaps));

            for (int i = end - 1; i >= start; i--) {
                comps++;
                boolean needSwap = arr[i] > arr[i + 1];
                String reason = needSwap
                        ? "arr[" + i + "] (" + arr[i] + ") > arr[" + (i + 1) + "] (" + arr[i + 1] + ") -> Out of order -> Swap!"
                        : "arr[" + i + "] (" + arr[i] + ") <= arr[" + (i + 1) + "] (" + arr[i + 1] + ") -> In order.";
                steps.add(new SortStep(arr, i, i + 1, start, false,
                        "Backward check: " + reason, comps, swaps));

                if (needSwap) {
                    int tmp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = tmp;
                    swaps++;
                    swapped = true;
                    steps.add(new SortStep(arr, i, i + 1, start, true,
                            "Swapped arr[" + i + "] and arr[" + (i + 1) + "].", comps, swaps));
                }
            }
            start++;
        }

        steps.add(new SortStep(arr, -1, -1, -1, false,
                "Cocktail Shaker Sort completed! Boundaries met from both sides.", comps, swaps));
        return steps;
    }

    // 15. Comb Sort
    public static List<SortStep> combSortDetailed(int[] arr) {
        List<SortStep> steps = new ArrayList<>();
        int n = arr.length;
        int comps = 0, swaps = 0;
        steps.add(new SortStep(arr, -1, -1, -1, false,
                "Initial array. Comb Sort eliminates 'turtles' using an optimal shrink factor of 1.3.", comps, swaps));

        int gap = n;
        final double SHRINK = 1.3;
        boolean swapped = true;

        while (gap > 1 || swapped) {
            gap = (int) (gap / SHRINK);
            if (gap < 1) gap = 1;

            swapped = false;
            steps.add(new SortStep(arr, -1, -1, -1, false,
                    "--- Gapped Sweep: GAP = " + gap + " ---", comps, swaps));

            for (int i = 0; i + gap < n; i++) {
                comps++;
                boolean needSwap = arr[i] > arr[i + gap];
                String reason = needSwap
                        ? "arr[" + i + "] (" + arr[i] + ") > arr[" + (i + gap) + "] (" + arr[i + gap] + ") -> Swap across gap " + gap + "!"
                        : "arr[" + i + "] (" + arr[i] + ") <= arr[" + (i + gap) + "] (" + arr[i + gap] + ") -> In order.";

                steps.add(new SortStep(arr, i, i + gap, -1, false,
                        "Comparing across gap " + gap + ": " + reason, comps, swaps));

                if (needSwap) {
                    int tmp = arr[i];
                    arr[i] = arr[i + gap];
                    arr[i + gap] = tmp;
                    swaps++;
                    swapped = true;
                    steps.add(new SortStep(arr, i, i + gap, -1, true,
                            "Swapped arr[" + i + "] and arr[" + (i + gap) + "] across gap " + gap + ".", comps, swaps));
                }
            }
        }

        steps.add(new SortStep(arr, -1, -1, -1, false,
                "Comb Sort completed! All gaps reduced down to 1.", comps, swaps));
        return steps;
    }

    // 16. Gnome Sort
    public static List<SortStep> gnomeSortDetailed(int[] arr) {
        List<SortStep> steps = new ArrayList<>();
        int n = arr.length;
        int comps = 0, swaps = 0;
        steps.add(new SortStep(arr, -1, -1, -1, false,
                "Initial array. Gnome Sort moves forward when elements are ordered, and steps backward after a swap.", comps, swaps));

        int pos = 0;
        while (pos < n) {
            if (pos == 0) {
                pos++;
            } else {
                comps++;
                if (arr[pos] >= arr[pos - 1]) {
                    steps.add(new SortStep(arr, pos - 1, pos, -1, false,
                            "arr[" + (pos - 1) + "] (" + arr[pos - 1] + ") <= arr[" + pos + "] (" + arr[pos] + ") -> In order! Gnome steps forward to index " + (pos + 1) + ".", comps, swaps));
                    pos++;
                } else {
                    int tmp = arr[pos];
                    arr[pos] = arr[pos - 1];
                    arr[pos - 1] = tmp;
                    swaps++;
                    steps.add(new SortStep(arr, pos - 1, pos, -1, true,
                            "arr[" + (pos - 1) + "] > arr[" + pos + "] -> Out of order! Swapped and gnome steps backward to inspect index " + (pos - 1) + ".", comps, swaps));
                    pos--;
                }
            }
        }

        steps.add(new SortStep(arr, -1, -1, -1, false,
                "Gnome Sort completed! Gnome reached the end of the flower pots.", comps, swaps));
        return steps;
    }

    public static int[][] bubbleSortSteps(int[] arr) { return to2D(bubbleSortDetailed(arr)); }
    public static int[][] selectionSortSteps(int[] arr) { return to2D(selectionSortDetailed(arr)); }
    public static int[][] insertionSortSteps(int[] arr) { return to2D(insertionSortDetailed(arr)); }

    private static int[][] to2D(List<SortStep> list) {
        int[][] res = new int[list.size()][];
        for (int i = 0; i < list.size(); i++) res[i] = list.get(i).array;
        return res;
    }
}
