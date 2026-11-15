package algexplorer.sorting;

/** One place that lists the sorting algorithms and their theory, so every screen uses the same list. */
public final class SortCatalog {
    private SortCatalog() {
    }

    /** Fresh objects of the six sorters: five algorithms, quicksort in two versions. Time: O(1). */
    public static Sorter[] all() {
        return new Sorter[] { new BubbleSort(), new SelectionSort(), new InsertionSort(), new MergeSort(),
                new QuickSort(false), new QuickSort(true) };
    }

    /** Rows of the complexity help table: name, best, average, worst, extra space, stable. */
    public static String[][] complexityRows() {
        return new String[][] {
                { "Bubble sort (early stop)", "O(n)", "O(n^2)", "O(n^2)", "O(1)", "Yes" },
                { "Selection sort", "O(n^2)", "O(n^2)", "O(n^2)", "O(1)", "No" },
                { "Insertion sort", "O(n)", "O(n^2)", "O(n^2)", "O(1)", "Yes" },
                { "Merge sort", "O(n log n)", "O(n log n)", "O(n log n)", "O(n)", "Yes" },
                { "Quicksort (first pivot)", "O(n log n)", "O(n log n)", "O(n^2) on sorted input", "O(log n)", "No" },
                { "Quicksort (random pivot)", "O(n log n)", "O(n log n) expected", "O(n^2), very unlikely", "O(log n)",
                        "No" } };
    }
}
