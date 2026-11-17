package algexplorer.ui;

import algexplorer.UIContext;
import algexplorer.navigation.Navigation;
import algexplorer.sorting.SortAlgorithms;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.util.*;

public class BenchmarkPane {

    public static class BenchmarkResult {
        public final String name;
        public final double timeMs;
        public final boolean skipped;
        public final String complexity;

        public BenchmarkResult(String name, double timeMs, boolean skipped, String complexity) {
            this.name = name;
            this.timeMs = timeMs;
            this.skipped = skipped;
            this.complexity = complexity;
        }
    }

    @FXML private Button backBtn;
    @FXML private Button exitBtn;

    @FXML private ComboBox<String> sizeCombo;
    @FXML private ComboBox<String> distCombo;
    @FXML private Button runBtn;
    @FXML private Label winnerLabel;

    @FXML private Canvas canvas;
    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;

    private final List<BenchmarkResult> results = new ArrayList<>();

    @FXML
    private void initialize() {
        backBtn.setOnAction(e -> goBack());
        exitBtn.setOnAction(e -> {
            if (UIContext.getStage() != null) UIContext.getStage().close();
        });

        sizeCombo.getItems().addAll("100 elements", "500 elements", "1,000 elements", "2,500 elements", "5,000 elements");
        sizeCombo.setValue("1,000 elements");

        distCombo.getItems().addAll("Random", "Nearly Sorted", "Reverse Sorted", "Many Duplicates");
        distCombo.setValue("Random");

        runBtn.setOnAction(e -> executeBenchmark());

        // Initial default render
        renderCanvas();
    }

    private void executeBenchmark() {
        runBtn.setDisable(true);
        progressBar.setVisible(true);
        progressBar.setProgress(-1);
        statusLabel.setText("Running benchmark across all algorithms on background thread...");
        results.clear();

        int n = switch (sizeCombo.getValue()) {
            case "100 elements" -> 100;
            case "500 elements" -> 500;
            case "1,000 elements" -> 1000;
            case "2,500 elements" -> 2500;
            case "5,000 elements" -> 5000;
            default -> 1000;
        };

        String dist = distCombo.getValue();

        Task<List<BenchmarkResult>> task = new Task<>() {
            @Override
            protected List<BenchmarkResult> call() {
                int[] base = generateArray(n, dist);
                List<BenchmarkResult> list = new ArrayList<>();

                // Helper to measure an algorithm
                benchmarkAlgo("Quick Sort", "O(n log n)", base, n, 50000, SortAlgorithms::quickSortDetailed, list);
                benchmarkAlgo("Merge Sort", "O(n log n)", base, n, 50000, SortAlgorithms::mergeSortDetailed, list);
                benchmarkAlgo("Heap Sort", "O(n log n)", base, n, 50000, SortAlgorithms::heapSortDetailed, list);
                benchmarkAlgo("Tim Sort", "O(n log n)", base, n, 50000, SortAlgorithms::timSortDetailed, list);
                benchmarkAlgo("Shell Sort", "O(n¹.⁵)", base, n, 50000, SortAlgorithms::shellSortDetailed, list);
                benchmarkAlgo("Radix Sort", "O(d·n)", base, n, 50000, SortAlgorithms::radixSortDetailed, list);
                benchmarkAlgo("Counting Sort", "O(n+k)", base, n, 50000, SortAlgorithms::countingSortDetailed, list);
                benchmarkAlgo("Bucket Sort", "O(n+k)", base, n, 50000, SortAlgorithms::bucketSortDetailed, list);

                // O(n^2) algorithms - skip if N > 2500 for safety
                benchmarkAlgo("Insertion Sort", "O(n²)", base, n, 3000, SortAlgorithms::insertionSortDetailed, list);
                benchmarkAlgo("Bubble Sort", "O(n²)", base, n, 2000, SortAlgorithms::bubbleSortDetailed, list);
                benchmarkAlgo("Selection Sort", "O(n²)", base, n, 2000, SortAlgorithms::selectionSortDetailed, list);
                benchmarkAlgo("Cycle Sort", "O(n²)", base, n, 2000, SortAlgorithms::cycleSortDetailed, list);

                return list;
            }
        };

        task.setOnSucceeded(e -> {
            results.clear();
            results.addAll(task.getValue());

            // Determine winner (fastest non-skipped)
            BenchmarkResult winner = null;
            for (BenchmarkResult r : results) {
                if (!r.skipped) {
                    if (winner == null || r.timeMs < winner.timeMs) {
                        winner = r;
                    }
                }
            }

            if (winner != null) {
                winnerLabel.setText("🏆 " + winner.name + " (" + String.format("%.2f", winner.timeMs) + " ms)");
            }

            statusLabel.setText("Benchmark completed successfully for N = " + n + " (" + dist + ").");
            progressBar.setVisible(false);
            runBtn.setDisable(false);
            renderCanvas();
        });

        task.setOnFailed(e -> {
            statusLabel.setText("Benchmark encountered an error: " + task.getException().getMessage());
            progressBar.setVisible(false);
            runBtn.setDisable(false);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FunctionalInterface
    interface AlgoRunner {
        void sort(int[] arr);
    }

    private void benchmarkAlgo(String name, String complexity, int[] base, int n, int maxN, AlgoRunner runner, List<BenchmarkResult> list) {
        if (n > maxN) {
            list.add(new BenchmarkResult(name, 0, true, complexity));
            return;
        }

        // Run warm-up
        int[] copy = Arrays.copyOf(base, base.length);
        runner.sort(copy);

        // Actual measurement (average of 3 runs)
        double totalTime = 0;
        int runs = 3;
        for (int r = 0; r < runs; r++) {
            copy = Arrays.copyOf(base, base.length);
            long start = System.nanoTime();
            runner.sort(copy);
            long end = System.nanoTime();
            totalTime += (end - start) / 1_000_000.0;
        }

        double avgMs = totalTime / runs;
        list.add(new BenchmarkResult(name, avgMs, false, complexity));
    }

    private int[] generateArray(int n, String dist) {
        int[] arr = new int[n];
        Random rng = new Random(42);

        switch (dist) {
            case "Random" -> {
                for (int i = 0; i < n; i++) arr[i] = rng.nextInt(n * 2);
            }
            case "Nearly Sorted" -> {
                for (int i = 0; i < n; i++) arr[i] = i * 2;
                for (int k = 0; k < n / 20; k++) {
                    int i = rng.nextInt(n);
                    int j = rng.nextInt(n);
                    int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
                }
            }
            case "Reverse Sorted" -> {
                for (int i = 0; i < n; i++) arr[i] = (n - i) * 2;
            }
            case "Many Duplicates" -> {
                for (int i = 0; i < n; i++) arr[i] = rng.nextInt(5);
            }
        }
        return arr;
    }

    private void renderCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        gc.clearRect(0, 0, w, h);

        if (results.isEmpty()) {
            gc.setFill(Color.web("#64748B"));
            gc.fillText("Select test parameters above and click 'Run Benchmark' to visualize performance metrics.", w * 0.22, h * 0.5);
            return;
        }

        double maxTime = 0.01;
        for (BenchmarkResult r : results) {
            if (!r.skipped && r.timeMs > maxTime) maxTime = r.timeMs;
        }

        int count = results.size();
        double sidePadding = 30;
        double usableW = w - (sidePadding * 2);
        double barGap = 16;
        double barWidth = (usableW - (barGap * (count - 1))) / count;
        double maxH = h - 90;
        double baseY = h - 45;

        // Find minimum time for winner
        double minTime = Double.MAX_VALUE;
        for (BenchmarkResult r : results) {
            if (!r.skipped && r.timeMs < minTime) minTime = r.timeMs;
        }

        for (int i = 0; i < count; i++) {
            BenchmarkResult r = results.get(i);
            double x = sidePadding + i * (barWidth + barGap);

            if (r.skipped) {
                // Skipped marker
                gc.setFill(Color.web("#334155", 0.5));
                gc.fillRoundRect(x, baseY - 20, barWidth, 20, 6, 6);
                gc.setFill(Color.web("#94A3B8"));
                gc.fillText("N/A", x + (barWidth / 2) - 10, baseY - 6);
            } else {
                double barH = Math.max(12, (r.timeMs / maxTime) * maxH);
                double y = baseY - barH;

                boolean isWinner = (Math.abs(r.timeMs - minTime) < 0.0001);

                LinearGradient grad = isWinner
                        ? new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#FFB703")), new Stop(1, Color.web("#FB8500")))
                        : new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#00F5D4")), new Stop(1, Color.web("#0077B6")));

                gc.setFill(grad);
                gc.fillRoundRect(x, y, barWidth, barH, 6, 6);

                // Time label above bar
                gc.setFill(isWinner ? Color.web("#FFB703") : Color.web("#E2E8F0"));
                String timeStr = String.format("%.2f ms", r.timeMs);
                gc.fillText(timeStr, Math.max(x - 6, x + (barWidth / 2) - (timeStr.length() * 3.5)), y - 6);

                if (isWinner) {
                    gc.fillText("★", x + (barWidth / 2) - 4, y - 18);
                }
            }

            // Algorithm name below bar
            gc.setFill(Color.web("#94A3B8"));
            String[] tokens = r.name.split(" ");
            String shortName = tokens[0];
            gc.fillText(shortName, x + (barWidth / 2) - (shortName.length() * 3.4), baseY + 18);
            gc.setFill(Color.web("#64748B"));
            gc.fillText(r.complexity, x + (barWidth / 2) - (r.complexity.length() * 3.2), baseY + 32);
        }
    }

    private void goBack() {
        Navigation.slideBack((Parent) backBtn.getScene().getRoot(), "/algexplorer/ui/HubPane.fxml");
    }
}
