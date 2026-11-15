package algexplorer.recursion;

/** One move in Tower of Hanoi: which disk, from which peg, to which peg. */
public class HanoiMove {
    public final int disk; // 1 is the smallest disk
    public final char from; // 'A', 'B' or 'C'
    public final char to;

    public HanoiMove(int disk, char from, char to) {
        this.disk = disk;
        this.from = from;
        this.to = to;
    }

    /** The text shown when a move is printed, e.g. "Move disk 1 from A to C". */
    @Override
    public String toString() {
        return "Move disk " + disk + " from " + from + " to " + to;
    }
}