package algexplorer.recursion;

public class HanoiDemo {
    public static void main(String[] args) {
        HanoiResult r = Hanoi.solve(3, 'A', 'C', 'B');
        System.out.println("3 disks, A to C, using B as the spare:");
        for (int i = 0; i < r.moves.length; i++) {
            System.out.println((i + 1) + ". " + r.moves[i]);
        }
        System.out.println("Moves = " + r.moves.length + ", calls = " + r.operations);
        System.out.println();

        for (int n = 1; n <= Hanoi.MAX_DISKS; n++) {
            HanoiResult x = Hanoi.solve(n, 'A', 'C', 'B');
            long expected = (1L << n) - 1;
            System.out.println(n + " disks: moves=" + x.moves.length
                    + "  expected(2^n - 1)=" + expected
                    + "  calls=" + x.operations
                    + "  " + (x.moves.length == expected ? "OK" : "MISMATCH"));
        }

        System.out.println();
        try {
            Hanoi.solve(9, 'A', 'C', 'B');
        } catch (IllegalArgumentException e) {
            System.out.println("9 disks rejected: " + e.getMessage());
        }
        try {
            Hanoi.solve(3, 'A', 'A', 'B');
        } catch (IllegalArgumentException e) {
            System.out.println("Same pegs rejected: " + e.getMessage());
        }
    }
}