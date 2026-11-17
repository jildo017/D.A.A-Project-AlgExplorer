package algexplorer.searching;

/**
 * Standard searching algorithms implemented completely from scratch
 * without using any Java built-in searching libraries or utilities.
 */
public class SearchingAlgorithms {

    /**
     * 1. Linear Search: O(n) Time, O(1) Space
     * Checks each element sequentially from index 0 to n - 1.
     */
    public static int linearSearch(int[] arr, int target) {
        if (arr == null) return -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 2. Binary Search: O(log n) Time, O(1) Space
     * Divides the sorted search window in half at each step.
     */
    public static int binarySearch(int[] arr, int target) {
        if (arr == null) return -1;
        int low = 0;
        int high = arr.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    /**
     * 3. Jump Search: O(√n) Time, O(1) Space
     * Jumps forward in blocks of optimal size √n, then performs linear search in the block.
     */
    public static int jumpSearch(int[] arr, int target) {
        if (arr == null || arr.length == 0) return -1;
        int n = arr.length;
        int step = (int) Math.floor(Math.sqrt(n));
        int prev = 0;

        while (prev < n && arr[Math.min(step, n) - 1] < target) {
            prev = step;
            step += (int) Math.floor(Math.sqrt(n));
            if (prev >= n) return -1;
        }

        int limit = Math.min(step, n);
        while (prev < limit) {
            if (arr[prev] == target) {
                return prev;
            }
            if (arr[prev] > target) {
                return -1;
            }
            prev++;
        }
        return -1;
    }

    /**
     * 4. Interpolation Search: O(log log n) Avg / O(n) Worst Time, O(1) Space
     * Estimates target position based on key distribution values.
     */
    public static int interpolationSearch(int[] arr, int target) {
        if (arr == null || arr.length == 0) return -1;
        int low = 0;
        int high = arr.length - 1;

        while (low <= high && target >= arr[low] && target <= arr[high]) {
            if (low == high) {
                return (arr[low] == target) ? low : -1;
            }

            long num = (long) (target - arr[low]) * (high - low);
            long den = (long) (arr[high] - arr[low]);
            if (den == 0) {
                return (arr[low] == target) ? low : -1;
            }

            int pos = (int) (low + (num / den));
            if (pos < low || pos > high) break;

            if (arr[pos] == target) {
                return pos;
            } else if (arr[pos] < target) {
                low = pos + 1;
            } else {
                high = pos - 1;
            }
        }
        return -1;
    }
}
