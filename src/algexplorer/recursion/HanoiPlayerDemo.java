package algexplorer.recursion;

public class HanoiPlayerDemo {
    public static void main(String[] args) {
        for (int n = 1; n <= Hanoi.MAX_DISKS; n++) {
            HanoiResult r = Hanoi.solve(n, 'A', 'C', 'B');
            HanoiPlayer p = new HanoiPlayer(n, 'A', 'C', r.moves);
            int count = 0;
            while (p.step()) {
                count++;
            }
            System.out.println(n + " disks: stepped " + count + " times, finished="
                    + p.isFinished() + ", all on C=" + p.isAtDestination());
        }

        HanoiResult r = Hanoi.solve(3, 'A', 'C', 'B');
        HanoiPlayer p = new HanoiPlayer(3, 'A', 'C', r.moves);
        p.step();
        p.step();
        System.out.println("After 2 steps: done=" + p.getDone() + " of " + p.getTotal()
                + ", last = " + p.getLastMove());
        p.reset();
        System.out.println("After reset: done=" + p.getDone() + ", last = " + p.getLastMove()
                + ", finished=" + p.isFinished());
        while (p.step()) {
        }
        System.out.println("Step after the end returns: " + p.step());
    }
}