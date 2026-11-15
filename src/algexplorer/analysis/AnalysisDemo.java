package algexplorer.analysis;

/** Console check of the experiment (no screen). Run it to see the tables quickly. */
public class AnalysisDemo {
    public static void main(String[] args) {
        boolean big = args.length > 0 && args[0].equals("big");
        ExperimentData d = ExperimentRunner.run(big, null);
        System.out.println(d.toTables());
        System.out.println(d.toGrowthTable());
    }
}
