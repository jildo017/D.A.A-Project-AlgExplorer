package algexplorer.recursion;

import algexplorer.core.OpCounter;
import algexplorer.core.Result;
import algexplorer.core.StopWatch;

public class Fibonacci {

    public static final int MAX_NAIVE = 30;

    public static final int MAX_FAST = 92; // fib(93) does not fit in a long

    private static void check(int n, int max) {
        if (n < 0) {
            throw new IllegalArgumentException("n cannot be negative.");
        }
        if (n > max) {
            throw new IllegalArgumentException("n must be " + max + " or less.");
        }
    }

    /** Naive recursion. Time: O(2^n). Space: O(n) for the call stack. */
    public static long naive(int n, OpCounter counter) {
        counter.add(); // count this function call
        if (n <= 1) { // base case: fib(0)=0, fib(1)=1
            return n;
        }
        return naive(n - 1, counter) + naive(n - 2, counter); // recursive case
    }

    /** Runs naive Fibonacci and returns answer, call count and time. */
    public static Result runNaive(int n) {
        check(n, MAX_NAIVE); // FIXED - was missing
        OpCounter counter = new OpCounter();
        StopWatch watch = new StopWatch();
        watch.start();
        long value = naive(n, counter);
        watch.stop();
        return new Result(value, counter.get(), watch.getNanos());
    }

    /** Memoized recursion. Time: O(n). Space: O(n) for memo array + call stack. */
    private static long memoHelper(int n, long[] memo, boolean[] known, OpCounter counter) {
        counter.add(); // count this function call
        if (n <= 1) { // base case
            return n;
        }
        if (known[n]) { // already worked out? reuse it
            return memo[n];
        }
        memo[n] = memoHelper(n - 1, memo, known, counter)
                + memoHelper(n - 2, memo, known, counter);
        known[n] = true; // remember it for next time
        return memo[n];
    }

    public static Result runMemo(int n) {
        check(n, MAX_FAST); // FIXED - was missing
        OpCounter counter = new OpCounter();
        StopWatch watch = new StopWatch();
        long[] memo = new long[n + 2]; // +2 so index 0 and 1 always exist
        boolean[] known = new boolean[n + 2];
        watch.start();
        long value = memoHelper(n, memo, known, counter);
        watch.stop();
        return new Result(value, counter.get(), watch.getNanos());
    }

    /** Bottom-up dynamic programming. Time: O(n). Space: O(1). */
    public static Result runBottomUp(int n) {
        check(n, MAX_FAST); // FIXED - was missing
        OpCounter counter = new OpCounter();
        StopWatch watch = new StopWatch();
        watch.start();
        long value;
        if (n <= 1) {
            value = n;
        } else {
            long previous = 0; // fib(0)
            long current = 1; // fib(1)
            for (int i = 2; i <= n; i++) {
                long next = previous + current;
                previous = current;
                current = next;
                counter.add(); // count each loop iteration
            }
            value = current;
        }
        watch.stop();
        return new Result(value, counter.get(), watch.getNanos());
    }
}