package algexplorer.sorting;

/**
 * One picture of the array during a sort: what it looks like and which parts
 * matter.
 */
public class SortStep {
    public final String description;
    public final int[] values; // the whole array at this moment
    public final int markA; // an element just compared, swapped or placed (-1 = none)
    public final int markB; // a second such element (-1 = none)
    public final int rangeLo; // the part of the array this step is about (-1 = none)
    public final int rangeHi;

    public SortStep(String description, int[] values, int markA, int markB, int rangeLo, int rangeHi) {
        this.description = description;
        this.values = values;
        this.markA = markA;
        this.markB = markB;
        this.rangeLo = rangeLo;
        this.rangeHi = rangeHi;
    }
}