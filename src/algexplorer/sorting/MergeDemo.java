package algexplorer.sorting;

import java.util.Random;

public class MergeDemo {
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

    public static void main(String[] args) {
        Sorter merge = new MergeSort();

        int[] sample = { 5, 2, 9, 1, 5, 6 };
        SortResult r = merge.sort(sample, true);
        System.out.println("=== " + r.algorithm + " on " + text(sample));
        for (int i = 0; i < r.steps.length; i++) {
            SortStep s = r.steps[i];
            System.out.println("  " + text(s.values) + "   " + s.description);
        }
        System.out.println("  comparisons=" + r.comparisons + "  moves=" + r.moves
                + "  sorted=" + ArrayTools.isSorted(r.sorted));
        System.out.println();

        int[] random = { 5, 2, 8, 1, 9, 3, 7, 4 };
        int[] sorted = { 1, 2, 3, 4, 5, 6, 7, 8 };
        int[] reversed = { 8, 7, 6, 5, 4, 3, 2, 1 };
        Sorter[] all = { new BubbleSort(), new SelectionSort(), new InsertionSort(), merge };
        System.out.println("Counts for 8 numbers (comparisons / swaps-or-moves):");
        System.out.println(String.format("%-16s %-12s %-12s %-12s", "", "random", "sorted", "reversed"));
        for (int i = 0; i < all.length; i++) {
            SortResult a = all[i].sort(random, false);
            SortResult b = all[i].sort(sorted, false);
            SortResult c = all[i].sort(reversed, false);
            System.out.println(String.format("%-16s %-12s %-12s %-12s", all[i].name(),
                    a.comparisons + " / " + a.moves, b.comparisons + " / " + b.moves,
                    c.comparisons + " / " + c.moves));
        }
        System.out.println();

        Random rnd = new Random(42);
        int tests = 0;
        boolean allGood = true;
        for (int round = 0; round < 300; round++) {
            int n = rnd.nextInt(31);
            int[] data = new int[n];
            for (int i = 0; i < n; i++) {
                data[i] = rnd.nextInt(41) - 20;
            }
            SortResult x = merge.sort(data, false);
            tests++;
            if (!ArrayTools.isSorted(x.sorted) || ArrayTools.sum(x.sorted) != ArrayTools.sum(data)
                    || x.sorted.length != data.length) {
                allGood = false;
                System.out.println("PROBLEM on " + text(data));
            }
        }
        System.out.println(
                tests + " random tests: " + (allGood ? "merge sort sorted every one correctly" : "SOME FAILED"));
        System.out.println();

        System.out.println("Growth of the comparisons on random data:");
        System.out.println(String.format("%-8s %-16s %-14s %-18s", "n", "merge sort", "n x log2(n)", "insertion sort"));
        int[] sizes = { 100, 1000, 10000 };
        for (int s = 0; s < sizes.length; s++) {
            int n = sizes[s];
            int[] data = new int[n];
            for (int i = 0; i < n; i++) {
                data[i] = rnd.nextInt(1_000_000);
            }
            SortResult m = merge.sort(data, false);
            SortResult ins = new InsertionSort().sort(data, false);
            long nlogn = Math.round(n * (Math.log(n) / Math.log(2)));
            System.out.println(String.format("%-8d %-16d %-14d %-18d", n, m.comparisons, nlogn, ins.comparisons));
        }
    }
}