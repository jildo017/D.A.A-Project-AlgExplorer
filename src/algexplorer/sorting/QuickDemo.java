package algexplorer.sorting;

import java.util.Random;

public class QuickDemo {
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
        System.out.println("  comparisons=" + r.comparisons + "  swaps=" + r.moves
                + "  sorted=" + ArrayTools.isSorted(r.sorted));
        System.out.println();
    }

    public static void main(String[] args) {
        Sorter first = new QuickSort(false);
        Sorter randomOne = new QuickSort(true, 7);

        int[] sample = { 5, 2, 9, 1, 5, 6 };
        showSteps(first, sample);
        showSteps(randomOne, sample);

        int[] random = { 5, 2, 8, 1, 9, 3, 7, 4 };
        int[] sorted = { 1, 2, 3, 4, 5, 6, 7, 8 };
        int[] reversed = { 8, 7, 6, 5, 4, 3, 2, 1 };
        Sorter[] all = { new BubbleSort(), new SelectionSort(), new InsertionSort(), new MergeSort(),
                first, new QuickSort(true, 7) };
        System.out.println("Counts for 8 numbers (comparisons / swaps-or-moves):");
        System.out.println(String.format("%-26s %-12s %-12s %-12s", "", "random", "sorted", "reversed"));
        for (int i = 0; i < all.length; i++) {
            SortResult a = all[i].sort(random, false);
            SortResult b = all[i].sort(sorted, false);
            SortResult c = all[i].sort(reversed, false);
            System.out.println(String.format("%-26s %-12s %-12s %-12s", all[i].name(),
                    a.comparisons + " / " + a.moves, b.comparisons + " / " + b.moves,
                    c.comparisons + " / " + c.moves));
        }
        System.out.println();

        Random rnd = new Random(42);
        Sorter[] two = { new QuickSort(false), new QuickSort(true, 99) };
        int tests = 0;
        boolean allGood = true;
        for (int round = 0; round < 300; round++) {
            int n = rnd.nextInt(31);
            int[] data = new int[n];
            for (int i = 0; i < n; i++) {
                data[i] = rnd.nextInt(11) - 5; // many repeated values
            }
            for (int s = 0; s < two.length; s++) {
                SortResult x = two[s].sort(data, false);
                tests++;
                if (!ArrayTools.isSorted(x.sorted) || ArrayTools.sum(x.sorted) != ArrayTools.sum(data)
                        || x.sorted.length != data.length) {
                    allGood = false;
                    System.out.println("PROBLEM: " + two[s].name() + " on " + text(data));
                }
            }
        }
        System.out.println(tests + " random tests with many repeats: "
                + (allGood ? "both quicksorts sorted every one correctly" : "SOME FAILED"));
        System.out.println();

        System.out.println("Comparisons for n = 5000 (first pivot against random pivot):");
        System.out.println(String.format("%-12s %-22s %-22s", "input", "first pivot", "random pivot"));
        int n = 5000;
        int[] randomData = new int[n];
        int[] sortedData = new int[n];
        int[] reversedData = new int[n];
        for (int i = 0; i < n; i++) {
            randomData[i] = rnd.nextInt(1_000_000);
            sortedData[i] = i;
            reversedData[i] = n - i;
        }
        String[] names = { "random", "sorted", "reversed" };
        int[][] inputs = { randomData, sortedData, reversedData };
        for (int k = 0; k < inputs.length; k++) {
            SortResult f = new QuickSort(false).sort(inputs[k], false);
            SortResult r = new QuickSort(true, 5).sort(inputs[k], false);
            System.out.println(String.format("%-12s %-22d %-22d", names[k], f.comparisons, r.comparisons));
        }
        System.out.println("n x (n - 1) / 2 = " + ((long) n * (n - 1) / 2) + "   n x log2(n) = "
                + Math.round(n * (Math.log(n) / Math.log(2))));
        System.out.println();

        int big = 100000;
        int[] bigSorted = new int[big];
        for (int i = 0; i < big; i++) {
            bigSorted[i] = i;
        }
        SortResult safe = new QuickSort(true, 5).sort(bigSorted, false);
        System.out.println("100,000 sorted numbers with a random pivot: sorted=" + ArrayTools.isSorted(safe.sorted)
                + ", comparisons=" + safe.comparisons + " (no stack overflow)");
    }
}