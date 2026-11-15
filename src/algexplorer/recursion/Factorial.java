package algexplorer.recursion;

import algexplorer.core.OpCounter;
import algexplorer.core.Result;
import algexplorer.core.StopWatch;

public class Factorial {
    public static final int MAX_N = 20; // 21! does not fit in a long

    /** Rejects bad input. Time: O(1). */
    private static void check(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n cannot be negative.");
        }
        if (n > MAX_N) {
            throw new IllegalArgumentException("n must be " + MAX_N + " or less.");
        }
    }

    /** Recursive factorial. Time: O(n). Space: O(n) for the call stack. */
    public static long recursive(int n, OpCounter counter) {
        counter.add(); // count this call
        if (n == 0) { // base case: 0! = 1
            return 1;
        }
        return n * recursive(n - 1, counter);
    }

    public static Result runRecursive(int n) {
        check(n);
        OpCounter counter = new OpCounter();
        StopWatch watch = new StopWatch();
        watch.start();
        long value = recursive(n, counter);
        watch.stop();
        return new Result(value, counter.get(), watch.getNanos());
    }

    /** Iterative factorial. Time: O(n). Space: O(1). */
    public static Result runIterative(int n) {
        check(n);
        OpCounter counter = new OpCounter();
        StopWatch watch = new StopWatch();
        watch.start();
        long value = 1;
        for (int i = 1; i <= n; i++) {
            value = value * i;
            counter.add(); // count each loop iteration
        }
        watch.stop();
        return new Result(value, counter.get(), watch.getNanos());
    }

    /** Builds the working, e.g. "5! = 5 x 4 x 3 x 2 x 1 = 120". Time: O(n). */
    public static String describe(int n) {
        check(n);
        if (n == 0) {
            return "0! = 1 (by definition: an empty product is 1)";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(n).append("! = ");
        for (int i = n; i >= 1; i--) {
            sb.append(i);
            if (i > 1) {
                sb.append(" x ");
            }
        }
        sb.append(" = ").append(runIterative(n).value);
        return sb.toString();
    }
}