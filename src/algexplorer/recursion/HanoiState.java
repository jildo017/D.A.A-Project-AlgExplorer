package algexplorer.recursion;

/** Which disks are on which peg right now. Each peg is a fixed array used as a stack. */
public class HanoiState {
    private final int[][] pegs;       // pegs[p][level] = disk size; level 0 is the bottom
    private final int[] height;       // how many disks are on each peg
    private final int totalDisks;

    /** All disks start on the source peg, biggest at the bottom. Time: O(n). */
    public HanoiState(int totalDisks, char source) {
        this.totalDisks = totalDisks;
        pegs = new int[3][totalDisks];
        height = new int[3];
        int s = index(source);
        for (int size = totalDisks; size >= 1; size--) {
            pegs[s][height[s]] = size;
            height[s]++;
        }
    }

    private static int index(char peg) {
        return peg - 'A';                 // 'A' -> 0, 'B' -> 1, 'C' -> 2
    }

    public int getTotalDisks() {
        return totalDisks;
    }

    /** How many disks are on this peg. Time: O(1). */
    public int count(char peg) {
        return height[index(peg)];
    }

    /** The disk at a level on a peg (level 0 is the bottom). Time: O(1). */
    public int diskAt(char peg, int level) {
        return pegs[index(peg)][level];
    }

    /** Carries out one move, and refuses it if it breaks a rule. Time: O(1). */
    public void apply(HanoiMove m) {
        int from = index(m.from);
        int to = index(m.to);
        if (height[from] == 0) {
            throw new IllegalStateException("Peg " + m.from + " is empty.");
        }
        int disk = pegs[from][height[from] - 1];            // the top disk
        if (disk != m.disk) {
            throw new IllegalStateException("The top disk on peg " + m.from
                    + " is disk " + disk + ", not disk " + m.disk + ".");
        }
        if (height[to] > 0 && pegs[to][height[to] - 1] < disk) {
            throw new IllegalStateException("Cannot put disk " + disk
                    + " on top of smaller disk " + pegs[to][height[to] - 1] + ".");
        }
        height[from]--;                                     // take it off
        pegs[to][height[to]] = disk;                        // put it on
        height[to]++;
    }

    /** True when all disks are on the given peg. */
    public boolean isComplete(char destination) {
        return height[index(destination)] == totalDisks;
    }
}