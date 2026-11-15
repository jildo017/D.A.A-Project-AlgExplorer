package algexplorer.recursion;

/**
 * Plays a list of Hanoi moves one at a time, on its own HanoiState. No window
 * code.
 */
public class HanoiPlayer {
    private final HanoiMove[] moves;
    private final int totalDisks;
    private final char source;
    private final char destination;
    private HanoiState state;
    private int done; // how many moves have been played

    public HanoiPlayer(int totalDisks, char source, char destination, HanoiMove[] moves) {
        this.totalDisks = totalDisks;
        this.source = source;
        this.destination = destination;
        this.moves = moves;
        reset();
    }

    /**
     * Goes back to the start: all disks on the source peg, no moves played. Time:
     * O(n).
     */
    public void reset() {
        state = new HanoiState(totalDisks, source);
        done = 0;
    }

    /** Plays the next move. Returns false if there is none left. Time: O(1). */
    public boolean step() {
        if (done >= moves.length) {
            return false;
        }
        state.apply(moves[done]); // HanoiState refuses any illegal move
        done++;
        return true;
    }

    public boolean isFinished() {
        return done == moves.length;
    }

    public boolean isAtDestination() {
        return state.isComplete(destination);
    }

    public int getDone() {
        return done;
    }

    public int getTotal() {
        return moves.length;
    }

    /** The move just played, or null if nothing has been played yet. */
    public HanoiMove getLastMove() {
        return done == 0 ? null : moves[done - 1];
    }

    public HanoiState getState() {
        return state;
    }
}