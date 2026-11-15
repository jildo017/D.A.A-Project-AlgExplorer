package algexplorer.recursion;

import algexplorer.core.OpCounter;
import algexplorer.core.StopWatch;

public class Hanoi {
    public static final int MAX_DISKS = 8;

    private final HanoiMove[] moves; // the list we fill in
    private int next = 0; // where the next move goes
    private final OpCounter counter = new OpCounter();

    private Hanoi(int n) {
        moves = new HanoiMove[(1 << n) - 1]; // room for exactly 2^n - 1 moves
    }

    /**
     * Moves n disks from 'from' to 'to' using 'via' as the spare peg.
     * Time: O(2^n). Space: O(n) for the call stack.
     */
    private void move(int n, char from, char to, char via) {
        counter.add(); // count this call
        if (n == 1) { // base case: just move the disk
            moves[next] = new HanoiMove(1, from, to);
            next++;
            return;
        }
        move(n - 1, from, via, to); // 1. top pile to the spare peg
        moves[next] = new HanoiMove(n, from, to); // 2. the biggest disk
        next++;
        move(n - 1, via, to, from); // 3. top pile onto the biggest
    }

    /** Checks the input. Time: O(1). */
    private static void check(int n, char source, char destination, char auxiliary) {
        if (n < 1 || n > MAX_DISKS) {
            throw new IllegalArgumentException(
                    "Number of disks must be from 1 to " + MAX_DISKS + ".");
        }
        if (!isPeg(source) || !isPeg(destination) || !isPeg(auxiliary)) {
            throw new IllegalArgumentException("Pegs must be A, B or C.");
        }
        if (source == destination || source == auxiliary || destination == auxiliary) {
            throw new IllegalArgumentException(
                    "Source, destination and auxiliary must be three different pegs.");
        }
    }

    private static boolean isPeg(char c) {
        return c == 'A' || c == 'B' || c == 'C';
    }

    /** Solves the puzzle and returns every move, the call count and the time. */
    public static HanoiResult solve(int n, char source, char destination, char auxiliary) {
        check(n, source, destination, auxiliary);
        Hanoi solver = new Hanoi(n);
        StopWatch watch = new StopWatch();
        watch.start();
        solver.move(n, source, destination, auxiliary);
        watch.stop();
        return new HanoiResult(solver.moves, solver.counter.get(), watch.getNanos());
    }
}