package algexplorer.sorting;

import java.util.Random;

public class SortDemo {
    private static String text(int[] a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(a[i]);
        }
        return sb.append("]").toString();
    }

    private static void showSteps(Sorter sorter, int[] data) {
        SortResult r = sorter.sort(data, true);
        System.out.println("=== " + r.algorithm + " on " + text(data));
        for (int i = 0; i < r.steps.length; i++) {
            SortStep s = r.steps[i];
            System.out.println("  " + text(s.values) + "   " + s.description);
        }
        System.out.println("  comparisons=" + r.comparisons + "  swaps/moves=" + r.moves
                + "  sorted=" + ArrayTools.isSorted(r.sorted));
        System.out.println();
    }

    public static void main(String[] args) {
        Sorter[] sorters = { new BubbleSort(), new SelectionSort(), new InsertionSort() };

        int[] sample = { 5, 2, 9, 1, 5, 6 };
        for (int i = 0; i < sorters.length; i++) {
            showSteps(sorters[i], sample);
        }

        int[] random = { 5, 2, 8, 1, 9, 3, 7, 4 };
        int[] sorted = { 1, 2, 3, 4, 5, 6, 7, 8 };
        int[] reversed = { 8, 7, 6, 5, 4, 3, 2, 1 };
        System.out.println("Counts for 8 numbers (comparisons / swaps-or-moves):");
        System.out.println(String.format("%-16s %-12s %-12s %-12s", "", "random", "sorted", "reversed"));
        for (int i = 0; i < sorters.length; i++) {
            SortResult a = sorters[i].sort(random, false);
            SortResult b = sorters[i].sort(sorted, false);
            SortResult c = sorters[i].sort(reversed, false);
            System.out.println(String.format("%-16s %-12s %-12s %-12s", sorters[i].name(),
                    a.comparisons + " / " + a.moves, b.comparisons + " / " + b.moves,
                    c.comparisons + " / " + c.moves));
        }
        System.out.println();

        Random random2 = new Random(42);
        int tests = 0;
        boolean allGood = true;
        for (int round = 0; round < 300; round++) {
            int n = random2.nextInt(31); // sizes 0 to 30, including empty and single
            int[] data = new int[n];
            for (int i = 0; i < n; i++) {
                data[i] = random2.nextInt(41) - 20; // negatives and repeats too
            }
            for (int s = 0; s < sorters.length; s++) {
                SortResult r = sorters[s].sort(data, false);
                tests++;
                if (!ArrayTools.isSorted(r.sorted) || ArrayTools.sum(r.sorted) != ArrayTools.sum(data)
                        || r.sorted.length != data.length) {
                    allGood = false;
                    System.out.println("PROBLEM: " + sorters[s].name() + " on " + text(data));
                }
            }
        }
        System.out.println(tests + " random tests (sizes 0 to 30, with negatives and repeats): "
                + (allGood ? "all sorted correctly" : "SOME FAILED"));

        int[] original = { 3, 1, 2 };
        new BubbleSort().sort(original, false);
        System.out.println("The input array is never changed: " + text(original));
    }
}