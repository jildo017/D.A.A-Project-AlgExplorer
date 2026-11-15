package algexplorer.core;

/** Counts basic operations (comparisons, swaps, calls). Each method is O(1). */
public class OpCounter {
    private long count;          // the running total, starts at 0

    public void add() {          // add 1 (use this most of the time)
        count++;
    }

    public void add(long amount) {   // add several at once
        count += amount;
    }

    public long get() {          // read the current total
        return count;
    }

    public void reset() {        // start again from 0
        count = 0;
    }
}