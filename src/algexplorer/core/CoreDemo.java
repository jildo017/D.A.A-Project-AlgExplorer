package algexplorer.core;

public class CoreDemo {
    public static void main(String[] args) {
        OpCounter counter = new OpCounter();
        StopWatch watch = new StopWatch();

        // One loop: should grow like n
        int[] sizes = { 1000, 2000 };
        for (int n : sizes) {
            counter.reset();
            watch.start();
            long sum = 0;
            for (int i = 1; i <= n; i++) {
                sum += i;
                counter.add();
            }
            watch.stop();
            System.out.println("One loop,    n=" + n + "  sum=" + sum
                    + "  operations=" + counter.get() + "  time=" + watch.getNanos() + " ns");
        }

        // Loop inside a loop: should grow like n * n
        int[] smallSizes = { 100, 200 };
        for (int n : smallSizes) {
            counter.reset();
            watch.start();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    counter.add();
                }
            }
            watch.stop();
            System.out.println("Nested loop, n=" + n
                    + "  operations=" + counter.get() + "  time=" + watch.getNanos() + " ns");
        }
    }
}