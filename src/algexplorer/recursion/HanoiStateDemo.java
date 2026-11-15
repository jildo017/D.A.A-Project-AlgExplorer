package algexplorer.recursion;

public class HanoiStateDemo {
    public static void main(String[] args) {
        // Play every solver answer and check that no move breaks a rule.
        for (int n = 1; n <= Hanoi.MAX_DISKS; n++) {
            HanoiResult r = Hanoi.solve(n, 'A', 'C', 'B');
            HanoiState s = new HanoiState(n, 'A');
            for (int i = 0; i < r.moves.length; i++) {
                s.apply(r.moves[i]);
            }
            System.out.println(n + " disks: all " + r.moves.length
                    + " moves legal, all disks on C = " + s.isComplete('C'));
        }

        // Two deliberately illegal moves.
        HanoiState s = new HanoiState(3, 'A');
        try {
            s.apply(new HanoiMove(3, 'A', 'B'));     // top of A is disk 1, not 3
        } catch (IllegalStateException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
        s.apply(new HanoiMove(1, 'A', 'B'));         // fine
        try {
            s.apply(new HanoiMove(2, 'A', 'B'));     // disk 2 onto smaller disk 1
        } catch (IllegalStateException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
}