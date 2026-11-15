package algexplorer.analysis;

import algexplorer.sorting.ArrayTools;
import algexplorer.sorting.SortCatalog;
import algexplorer.sorting.SortResult;
import algexplorer.sorting.Sorter;
import java.util.Random;

/**
 * Module D back end. Times every sorter on random, sorted and reverse-sorted input of growing size, three runs each,
 * and keeps the average. No screen code here, so it can be tested alone.
 */
public class ExperimentRunner {
    public static final int[] SIZES = { 100, 500, 1000, 2000, 5000, 10000 };
    public static final int[] BIG_SIZES = { 50000, 100000 };
    public static final String[] INPUTS = { "Random", "Sorted", "Reverse sorted" };
    public static final int REPEATS = 3;

    /** Lets the screen follow the work and cancel it. */
    public interface Progress {
        void update(int done, int total, String text);

        boolean cancelled();
    }

    /** Bubble, selection and insertion (the first three sorters) are O(n^2), so they skip the big sizes. */
    private static boolean isQuadratic(int algo) {
        return algo <= 2;
    }

    /** Runs the whole experiment. Time: sum of all sorts. includeBig adds n = 50,000 and 100,000 (merge, quick only). */
    public static ExperimentData run(boolean includeBig, Progress progress) {
        int bigCount = includeBig ? BIG_SIZES.length : 0;
        int[] sizes = new int[SIZES.length + bigCount];
        for (int i = 0; i < SIZES.length; i++) {
            sizes[i] = SIZES[i];
        }
        for (int i = 0; i < bigCount; i++) {
            sizes[SIZES.length + i] = BIG_SIZES[i];
        }

        Sorter[] sorters = SortCatalog.all();
        String[] names = new String[sorters.length];
        for (int a = 0; a < sorters.length; a++) {
            names[a] = sorters[a].name();
        }
        ExperimentData data = new ExperimentData(sizes, names, INPUTS);

        // warm-up: sort several mid-sized arrays of every kind so the JVM has compiled the loops before we measure
        for (int round = 0; round < 6; round++) {
            for (int kind = 0; kind < INPUTS.length; kind++) {
                int[] warm = makeInput(kind, 3000, 100 + round);
                for (int a = 0; a < sorters.length; a++) {
                    sorters[a].sort(warm, false);
                }
            }
        }

        int total = 0;
        for (int s = 0; s < sizes.length; s++) {
            for (int a = 0; a < sorters.length; a++) {
                if (!(isQuadratic(a) && sizes[s] > 10000)) {
                    total += INPUTS.length * REPEATS;
                }
            }
        }

        int done = 0;
        for (int i = 0; i < INPUTS.length; i++) {
            for (int s = 0; s < sizes.length; s++) {
                double[] sum = new double[sorters.length];
                for (int rep = 0; rep < REPEATS; rep++) {
                    int[] input = makeInput(i, sizes[s], rep);
                    for (int a = 0; a < sorters.length; a++) {
                        if (isQuadratic(a) && sizes[s] > 10000) {
                            continue;
                        }
                        if (progress != null && progress.cancelled()) {
                            return data;
                        }
                        if (progress != null) {
                            progress.update(done, total, names[a] + ", " + INPUTS[i] + ", n = " + sizes[s]
                                    + ", run " + (rep + 1));
                        }
                        SortResult r = sorters[a].sort(input, false);
                        if (!ArrayTools.isSorted(r.sorted)) {
                            throw new IllegalStateException(names[a] + " did not sort correctly");
                        }
                        sum[a] += r.nanos / 1_000_000.0;
                        done++;
                    }
                }
                for (int a = 0; a < sorters.length; a++) {
                    if (!(isQuadratic(a) && sizes[s] > 10000)) {
                        data.ms[a][i][s] = sum[a] / REPEATS;
                    }
                }
            }
        }
        if (progress != null) {
            progress.update(total, total, "Finished");
        }
        return data;
    }

    /** kind: 0 random, 1 already sorted, 2 reverse sorted. A different random array for every repeat. */
    static int[] makeInput(int kind, int n, int repeat) {
        int[] a = new int[n];
        Random rnd = new Random(1000L * n + repeat);
        for (int i = 0; i < n; i++) {
            if (kind == 0) {
                a[i] = rnd.nextInt(1_000_000);
            } else if (kind == 1) {
                a[i] = i;
            } else {
                a[i] = n - i;
            }
        }
        return a;
    }
}
