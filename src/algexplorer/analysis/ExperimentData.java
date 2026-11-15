package algexplorer.analysis;

/** The results of one experiment: average milliseconds for every algorithm, input kind and size. */
public class ExperimentData {
    public final int[] sizes;
    public final String[] algorithms;
    public final String[] inputs;
    /** ms[algorithm][input][size]; NaN means "not run". */
    public final double[][][] ms;

    public ExperimentData(int[] sizes, String[] algorithms, String[] inputs) {
        this.sizes = sizes;
        this.algorithms = algorithms;
        this.inputs = inputs;
        this.ms = new double[algorithms.length][inputs.length][sizes.length];
        for (int a = 0; a < algorithms.length; a++) {
            for (int i = 0; i < inputs.length; i++) {
                for (int s = 0; s < sizes.length; s++) {
                    ms[a][i][s] = Double.NaN;
                }
            }
        }
    }

    /** The data as CSV text (one row per measurement), ready for Excel. Time: O(total cells). */
    public String toCsv() {
        StringBuilder sb = new StringBuilder("algorithm,input,n,average_ms\n");
        for (int a = 0; a < algorithms.length; a++) {
            for (int i = 0; i < inputs.length; i++) {
                for (int s = 0; s < sizes.length; s++) {
                    if (!Double.isNaN(ms[a][i][s])) {
                        sb.append(algorithms[a]).append(',').append(inputs[i]).append(',').append(sizes[s])
                                .append(',').append(String.format("%.6f", ms[a][i][s])).append('\n');
                    }
                }
            }
        }
        return sb.toString();
    }

    /** One table per input kind (rows = algorithms, columns = n). */
    public String toTables() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inputs.length; i++) {
            sb.append("Average time in ms, ").append(inputs[i]).append(" input\n");
            sb.append(String.format("%-26s", "n ->"));
            for (int s = 0; s < sizes.length; s++) {
                sb.append(String.format("%12d", sizes[s]));
            }
            sb.append('\n');
            for (int a = 0; a < algorithms.length; a++) {
                sb.append(String.format("%-26s", algorithms[a]));
                for (int s = 0; s < sizes.length; s++) {
                    sb.append(Double.isNaN(ms[a][i][s]) ? String.format("%12s", "-")
                            : String.format("%12.3f", ms[a][i][s]));
                }
                sb.append('\n');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Compares measured growth with theory: how many times slower when n doubles from 5,000 to 10,000. Expected:
     * about 4 for n^2, about 2.16 for n log n, about 2 for n.
     */
    public String toGrowthTable() {
        int s5 = indexOf(5000);
        int s10 = indexOf(10000);
        if (s5 < 0 || s10 < 0) {
            return "";
        }
        // expected growth factor for [algorithm][input: random, sorted, reversed]
        double q = 4.0, l = 2.0, nl = 10000 * Math.log(10000) / (5000 * Math.log(5000));
        double[][] expected = { { q, l, q }, { q, q, q }, { q, l, q }, { nl, nl, nl }, { nl, q, q }, { nl, nl, nl } };
        StringBuilder sb = new StringBuilder("Growth when n doubles (5,000 -> 10,000): measured / theory\n");
        sb.append(String.format("%-26s", ""));
        for (int i = 0; i < inputs.length; i++) {
            sb.append(String.format("%-22s", inputs[i]));
        }
        sb.append('\n');
        for (int a = 0; a < algorithms.length; a++) {
            sb.append(String.format("%-26s", algorithms[a]));
            for (int i = 0; i < inputs.length; i++) {
                double t5 = ms[a][i][s5];
                double t10 = ms[a][i][s10];
                String cell = "-";
                if (!Double.isNaN(t5) && !Double.isNaN(t10) && t5 > 0) {
                    cell = String.format("%.2f / %.2f", t10 / t5, expected[a][i]);
                }
                sb.append(String.format("%-22s", cell));
            }
            sb.append('\n');
        }
        sb.append("(Times below about 0.05 ms are noisy, so those ratios can be far from the theory.)\n");
        return sb.toString();
    }

    private int indexOf(int n) {
        for (int s = 0; s < sizes.length; s++) {
            if (sizes[s] == n) {
                return s;
            }
        }
        return -1;
    }
}
