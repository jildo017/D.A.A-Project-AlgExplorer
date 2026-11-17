package utils;

/** Returns {Best, Average, Worst, Space, Explanation} for an algorithm name. */
public class ComplexityInfo {
    public static final int BEST = 0;
    public static final int AVERAGE = 1;
    public static final int WORST = 2;
    public static final int SPACE = 3;
    public static final int EXPLANATION = 4;

    private ComplexityInfo() {
    }

    private static String[] make(String best, String avg, String worst, String space, String why) {
        return new String[] {best, avg, worst, space, why};
    }

    public static String[] getInfo(String algorithmName) {
        if (algorithmName == null) {
            algorithmName = "";
        }
        switch (algorithmName) {
            case "Bubble Sort":
                return make("O(n)", "O(n^2)", "O(n^2)", "O(1)",
                        "Repeatedly swaps adjacent out-of-order pairs; stops early if a pass makes no swaps.");
            case "Selection Sort":
                return make("O(n^2)", "O(n^2)", "O(n^2)", "O(1)",
                        "Always scans the whole unsorted part to find the minimum, so it never gets faster.");
            case "Insertion Sort":
                return make("O(n)", "O(n^2)", "O(n^2)", "O(1)",
                        "Inserts each element into the sorted prefix; very fast on nearly sorted data.");
            case "Shell Sort":
                return make("O(n log n)", "O(n^1.5)", "O(n^2)", "O(1)",
                        "Insertion sort over shrinking gaps (n/2, n/4, ... 1) so far-apart items move quickly.");
            case "Merge Sort":
                return make("O(n log n)", "O(n log n)", "O(n log n)", "O(n)",
                        "Splits the array in half, sorts each half, then merges them using temporary arrays.");
            case "Quick Sort":
                return make("O(n log n)", "O(n log n)", "O(n^2)", "O(log n)",
                        "Partitions around a pivot (Lomuto); a bad pivot on sorted data gives the O(n^2) worst case.");
            case "Heap Sort":
                return make("O(n log n)", "O(n log n)", "O(n log n)", "O(1)",
                        "Builds a max-heap in the array, then repeatedly moves the largest item to the end.");
            case "Counting Sort":
                return make("O(n + k)", "O(n + k)", "O(n + k)", "O(n + k)",
                        "Counts how often each value occurs (k = largest value) and rebuilds the array; no comparisons.");
            case "Radix Sort":
                return make("O(d(n + b))", "O(d(n + b))", "O(d(n + b))", "O(n + b)",
                        "Sorts digit by digit (base b = 10, d digits) using a stable counting sort each pass.");
            case "Bucket Sort":
                return make("O(n + k)", "O(n + k)", "O(n^2)", "O(n + k)",
                        "Spreads values into buckets, insertion-sorts each bucket; one crowded bucket is the worst case.");
            case "Pancake Sort":
                return make("O(n)", "O(n^2)", "O(n^2)", "O(1)",
                        "Repeatedly finds the maximum element and reverses prefix sub-arrays (flips) to move items into position.");
            case "Custom Stack":
                return make("O(1)", "O(1)", "O(1)", "O(n)",
                        "Push, pop and peek only touch the top node (LIFO), so each takes constant time.");
            case "Custom Queue":
                return make("O(1)", "O(1)", "O(1)", "O(n)",
                        "Enqueue works at the rear and dequeue at the front (FIFO), both in constant time.");
            case "Custom Linked List":
                return make("O(1)", "O(n)", "O(n)", "O(n)",
                        "Inserting at the start is O(1), but search, delete and insert-at-end must walk the list.");
            case "Factorial":
                return make("O(n)", "O(n)", "O(n)", "O(n)",
                        "Makes one recursive call per value from n down to 1, so the call stack grows to depth n.");
            case "Fibonacci":
                return make("O(2^n)", "O(2^n)", "O(2^n)", "O(n)",
                        "Each call spawns two more calls, so work roughly doubles with n; the stack depth is only n.");
            case "Tower of Hanoi":
                return make("O(2^n)", "O(2^n)", "O(2^n)", "O(n)",
                        "Needs exactly 2^n - 1 moves for n disks; recursion depth is n.");
            case "Recursive Binary Search":
                return make("O(1)", "O(log n)", "O(log n)", "O(log n)",
                        "Halves the search range on every call, so at most about log2(n) calls are needed.");
            case "Linear Search":
                return make("O(1)", "O(n)", "O(n)", "O(1)",
                        "Checks every element one by one from the start until the target is found.");
            case "Binary Search":
                return make("O(1)", "O(log n)", "O(log n)", "O(1)",
                        "Needs sorted data; looks at the middle and discards half of the range each step.");
            case "Jump Search":
                return make("O(1)", "O(sqrt n)", "O(sqrt n)", "O(1)",
                        "Needs sorted data; jumps ahead in blocks of sqrt(n), then scans one block linearly.");
            case "Interpolation Search":
                return make("O(1)", "O(log log n)", "O(n)", "O(1)",
                        "Needs sorted data; guesses the position from the target's value, fast on evenly spread numbers.");
            default:
                return make("N/A", "N/A", "N/A", "N/A", "No complexity information available.");
        }
    }
}
