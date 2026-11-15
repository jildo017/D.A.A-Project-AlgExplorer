package algexplorer.sorting;

/**
 * Small array helpers written by hand (no ready-made copy or sort functions).
 */
public final class ArrayTools {
    private ArrayTools() {
    }

    /** A new array with the same values. Time: O(n). */
    public static int[] copy(int[] a) {
        int[] b = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            b[i] = a[i];
        }
        return b;
    }

    /**
     * True if every value is less than or equal to the one after it. Time: O(n).
     */
    public static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1] > a[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * The sum of all values, used to check that a sort did not lose or invent
     * numbers. Time: O(n).
     */
    public static long sum(int[] a) {
        long total = 0;
        for (int i = 0; i < a.length; i++) {
            total += a[i];
        }
        return total;
    }
}