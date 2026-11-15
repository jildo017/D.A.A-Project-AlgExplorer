package algexplorer.sorting;

/**
 * A growing list of SortStep objects, kept in a plain array that we enlarge by
 * hand.
 */
public class StepLog {
    public static final int MAX_STEPS = 2000; // protects memory if someone sorts a huge array with steps on

    private SortStep[] items = new SortStep[16];
    private int count = 0;
    private boolean cut = false;

    /** Saves a copy of the array with its description. Time: O(n). */
    public void add(String description, int[] values, int markA, int markB, int rangeLo, int rangeHi) {
        if (count >= MAX_STEPS) {
            cut = true;
            return;
        }
        if (count == items.length) {
            SortStep[] bigger = new SortStep[items.length * 2];
            for (int i = 0; i < count; i++) {
                bigger[i] = items[i];
            }
            items = bigger;
        }
        items[count] = new SortStep(description, ArrayTools.copy(values), markA, markB, rangeLo, rangeHi);
        count++;
    }

    public SortStep[] toArray() {
        SortStep[] result = new SortStep[count];
        for (int i = 0; i < count; i++) {
            result[i] = items[i];
        }
        return result;
    }

    /** True if some steps were not saved because MAX_STEPS was reached. */
    public boolean wasCut() {
        return cut;
    }
}