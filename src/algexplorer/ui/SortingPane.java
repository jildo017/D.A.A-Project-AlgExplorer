package algexplorer.ui;

import algexplorer.UIContext;
import algexplorer.navigation.Navigation;
import algexplorer.sorting.SortAlgorithms;
import algexplorer.sorting.SortAlgorithms.SortStep;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SortingPane {

    public enum AlgoDefinition {
        BUBBLE("Bubble Sort", "O(n²)", "O(1)"),
        SELECTION("Selection Sort", "O(n²)", "O(1)"),
        INSERTION("Insertion Sort", "O(n²)", "O(1)"),
        SHELL("Shell Sort", "O(n¹.⁵)", "O(1)"),
        MERGE("Merge Sort", "O(n log n)", "O(n)"),
        QUICK("Quick Sort", "O(n log n)", "O(log n)"),
        HEAP("Heap Sort", "O(n log n)", "O(1)"),
        COCKTAIL("Cocktail Shaker Sort", "O(n²)", "O(1)"),
        COUNTING("Counting Sort", "O(n + k)", "O(k)"),
        RADIX("Radix Sort", "O(d·(n + k))", "O(n + k)"),
        BUCKET("Bucket Sort", "O(n + k)", "O(n)"),
        CYCLE("Cycle Sort", "O(n²)", "O(1)"),
        TIM("Tim Sort", "O(n log n)", "O(n)"),
        PANCAKE("Pancake Sort", "O(n²)", "O(1)"),
        COMB("Comb Sort", "O(n log n)", "O(1)"),
        GNOME("Gnome Sort", "O(n²)", "O(1)");

        public final String displayName;
        public final String timeComplexity;
        public final String spaceComplexity;

        AlgoDefinition(String displayName, String timeComplexity, String spaceComplexity) {
            this.displayName = displayName;
            this.timeComplexity = timeComplexity;
            this.spaceComplexity = spaceComplexity;
        }
    }

    @FXML private Button backBtn;
    @FXML private Button exitBtn;

    @FXML private Label algoNameStat;
    @FXML private Label algoComplexityStat;

    // 16 Algorithm Buttons
    @FXML private Button btnBubble;
    @FXML private Button btnSelection;
    @FXML private Button btnInsertion;
    @FXML private Button btnShell;
    @FXML private Button btnMerge;
    @FXML private Button btnQuick;
    @FXML private Button btnHeap;
    @FXML private Button btnCocktail;
    @FXML private Button btnCounting;
    @FXML private Button btnRadix;
    @FXML private Button btnBucket;
    @FXML private Button btnCycle;
    @FXML private Button btnTim;
    @FXML private Button btnPancake;
    @FXML private Button btnComb;
    @FXML private Button btnGnome;

    private Button[] allAlgoButtons;

    // Array Inputs
    @FXML private TextField inputField;
    @FXML private Button loadBtn;
    @FXML private Button randomBtn;
    @FXML private Button nearlySortedBtn;
    @FXML private Button reversedBtn;

    // Canvas
    @FXML private Canvas canvas;

    // Actions & Narration
    @FXML private Label actionText;
    @FXML private Label stepCounterLabel;

    // Playback Controls
    @FXML private Button playBtn;
    @FXML private Button stepBackBtn;
    @FXML private Button stepForwardBtn;
    @FXML private Button resetBtn;
    @FXML private Slider speedSlider;
    @FXML private Label speedValueLabel;

    @FXML private Label comparisonsLabel;
    @FXML private Label swapsLabel;

    private AlgoDefinition activeAlgo = AlgoDefinition.BUBBLE;
    public static AlgoDefinition pendingAlgo = null;
    private int[] baseArray;
    private List<SortStep> steps = new ArrayList<>();
    private int currentStep = 0;
    private Timeline timeline;
    private boolean isPlaying = false;

    private Button getBtnForAlgo(AlgoDefinition algo) {
        return switch (algo) {
            case BUBBLE -> btnBubble;
            case SELECTION -> btnSelection;
            case INSERTION -> btnInsertion;
            case SHELL -> btnShell;
            case MERGE -> btnMerge;
            case QUICK -> btnQuick;
            case HEAP -> btnHeap;
            case COCKTAIL -> btnCocktail;
            case COUNTING -> btnCounting;
            case RADIX -> btnRadix;
            case BUCKET -> btnBucket;
            case CYCLE -> btnCycle;
            case TIM -> btnTim;
            case PANCAKE -> btnPancake;
            case COMB -> btnComb;
            case GNOME -> btnGnome;
        };
    }

    @FXML
    private void initialize() {
        allAlgoButtons = new Button[]{
                btnBubble, btnSelection, btnInsertion, btnShell, btnMerge, btnQuick,
                btnHeap, btnCocktail, btnCounting, btnRadix, btnBucket, btnCycle,
                btnTim, btnPancake, btnComb, btnGnome
        };

        // Wire 16 Algorithm buttons
        btnBubble.setOnAction(e -> selectAlgo(AlgoDefinition.BUBBLE, btnBubble));
        btnSelection.setOnAction(e -> selectAlgo(AlgoDefinition.SELECTION, btnSelection));
        btnInsertion.setOnAction(e -> selectAlgo(AlgoDefinition.INSERTION, btnInsertion));
        btnShell.setOnAction(e -> selectAlgo(AlgoDefinition.SHELL, btnShell));
        btnMerge.setOnAction(e -> selectAlgo(AlgoDefinition.MERGE, btnMerge));
        btnQuick.setOnAction(e -> selectAlgo(AlgoDefinition.QUICK, btnQuick));
        btnHeap.setOnAction(e -> selectAlgo(AlgoDefinition.HEAP, btnHeap));
        btnCocktail.setOnAction(e -> selectAlgo(AlgoDefinition.COCKTAIL, btnCocktail));
        btnCounting.setOnAction(e -> selectAlgo(AlgoDefinition.COUNTING, btnCounting));
        btnRadix.setOnAction(e -> selectAlgo(AlgoDefinition.RADIX, btnRadix));
        btnBucket.setOnAction(e -> selectAlgo(AlgoDefinition.BUCKET, btnBucket));
        btnCycle.setOnAction(e -> selectAlgo(AlgoDefinition.CYCLE, btnCycle));
        btnTim.setOnAction(e -> selectAlgo(AlgoDefinition.TIM, btnTim));
        btnPancake.setOnAction(e -> selectAlgo(AlgoDefinition.PANCAKE, btnPancake));
        btnComb.setOnAction(e -> selectAlgo(AlgoDefinition.COMB, btnComb));
        btnGnome.setOnAction(e -> selectAlgo(AlgoDefinition.GNOME, btnGnome));

        // Navigation
        backBtn.setOnAction(e -> goBack());
        exitBtn.setOnAction(e -> {
            if (UIContext.getStage() != null) UIContext.getStage().close();
        });

        // Presets & Load
        loadBtn.setOnAction(e -> parseAndLoadArray());
        randomBtn.setOnAction(e -> generateRandom(16));
        nearlySortedBtn.setOnAction(e -> generateNearlySorted(16));
        reversedBtn.setOnAction(e -> generateReversed(16));

        // Playback
        playBtn.setOnAction(e -> togglePlay());
        stepForwardBtn.setOnAction(e -> stepForward());
        stepBackBtn.setOnAction(e -> stepBackward());
        resetBtn.setOnAction(e -> resetToStart());

        // Default to comfortable study pace (750 ms)
        speedSlider.setValue(750);
        updateSpeedLabel(750);

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int ms = newVal.intValue();
            updateSpeedLabel(ms);
            if (isPlaying) {
                timeline.stop();
                startTimeline();
            }
        });

        // Select initial algorithm (pending or default Bubble)
        if (pendingAlgo != null) {
            selectAlgo(pendingAlgo, getBtnForAlgo(pendingAlgo));
            pendingAlgo = null;
        } else {
            selectAlgo(AlgoDefinition.BUBBLE, btnBubble);
        }
        parseAndLoadArray();
    }

    private void updateSpeedLabel(int ms) {
        String pace;
        if (ms < 300) pace = " (Fast)";
        else if (ms > 1200) pace = " (Slow-Mo)";
        else pace = " (Study Pace)";
        speedValueLabel.setText(ms + " ms" + pace);
    }

    private void selectAlgo(AlgoDefinition algo, Button selectedBtn) {
        stopAnimation();
        activeAlgo = algo;

        for (Button btn : allAlgoButtons) {
            btn.getStyleClass().remove("locked");
        }
        selectedBtn.getStyleClass().add("locked");

        algoNameStat.setText(algo.displayName);
        algoComplexityStat.setText(algo.timeComplexity);
        actionText.setText("Selected " + algo.displayName + " (" + algo.timeComplexity + "). Ready to trace step-by-step.");

        recalculateSteps();
    }

    private void generateRandom(int count) {
        stopAnimation();
        Random rng = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(10 + rng.nextInt(90));
            if (i < count - 1) sb.append(", ");
        }
        inputField.setText(sb.toString());
        parseAndLoadArray();
    }

    private void generateNearlySorted(int count) {
        stopAnimation();
        Random rng = new Random();
        int[] arr = new int[count];
        for (int i = 0; i < count; i++) {
            arr[i] = (i + 1) * 6;
        }
        for (int k = 0; k < 2; k++) {
            int i = rng.nextInt(count);
            int j = rng.nextInt(count);
            int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(arr[i]);
            if (i < count - 1) sb.append(", ");
        }
        inputField.setText(sb.toString());
        parseAndLoadArray();
    }

    private void generateReversed(int count) {
        stopAnimation();
        StringBuilder sb = new StringBuilder();
        for (int i = count; i >= 1; i--) {
            sb.append(i * 6);
            if (i > 1) sb.append(", ");
        }
        inputField.setText(sb.toString());
        parseAndLoadArray();
    }

    private void parseAndLoadArray() {
        stopAnimation();
        String text = inputField.getText().trim();
        if (text.isEmpty()) {
            actionText.setText("Error: Input is empty.");
            return;
        }

        String[] tokens = text.split("[,\\s]+");
        List<Integer> list = new ArrayList<>();
        for (String t : tokens) {
            if (t.isEmpty()) continue;
            try {
                list.add(Integer.parseInt(t));
            } catch (NumberFormatException e) {
                actionText.setText("Error: Invalid number '" + t + "'. Integers only.");
                return;
            }
        }

        if (list.isEmpty()) {
            actionText.setText("Error: No valid numbers found.");
            return;
        }

        if (list.size() > 100) {
            actionText.setText("Warning: Exceeded 100 elements. Trimmed to first 100.");
            list = list.subList(0, 100);
        }

        baseArray = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            baseArray[i] = list.get(i);
        }

        recalculateSteps();
    }

    private void recalculateSteps() {
        stopAnimation();
        if (baseArray == null || baseArray.length == 0) return;

        steps = SortAlgorithms.getSteps(activeAlgo.displayName, baseArray);
        currentStep = 0;
        updateUI();
        renderCanvas();
    }

    private void togglePlay() {
        if (isPlaying) {
            stopAnimation();
        } else {
            startAnimation();
        }
    }

    private void startAnimation() {
        if (steps == null || steps.isEmpty()) return;
        if (currentStep >= steps.size() - 1) {
            currentStep = 0;
        }

        isPlaying = true;
        playBtn.setText("⏸ Pause");
        startTimeline();
    }

    private void startTimeline() {
        double delay = speedSlider.getValue();
        timeline = new Timeline(new KeyFrame(Duration.millis(delay), e -> {
            if (currentStep < steps.size() - 1) {
                currentStep++;
                updateUI();
                renderCanvas();
            } else {
                stopAnimation();
                actionText.setText("🎉 " + activeAlgo.displayName + " completed! Array is fully sorted in " + (steps.size() - 1) + " steps.");
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void stopAnimation() {
        if (timeline != null) timeline.stop();
        isPlaying = false;
        playBtn.setText("▶ Start Sorting");
    }

    private void stepForward() {
        stopAnimation();
        if (steps != null && currentStep < steps.size() - 1) {
            currentStep++;
            updateUI();
            renderCanvas();
        }
    }

    private void stepBackward() {
        stopAnimation();
        if (steps != null && currentStep > 0) {
            currentStep--;
            updateUI();
            renderCanvas();
        }
    }

    private void resetToStart() {
        stopAnimation();
        currentStep = 0;
        updateUI();
        renderCanvas();
        actionText.setText("Reset to step 0. Press Start to begin study pace trace.");
    }

    private void updateUI() {
        if (steps == null || steps.isEmpty()) return;
        SortStep s = steps.get(currentStep);

        stepCounterLabel.setText("Step " + currentStep + " / " + (steps.size() - 1));
        actionText.setText(s.description);
        comparisonsLabel.setText(String.valueOf(s.comparisons));
        swapsLabel.setText(String.valueOf(s.swaps));
    }

    private void renderCanvas() {
        if (steps == null || steps.isEmpty() || currentStep >= steps.size()) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        gc.clearRect(0, 0, w, h);

        SortStep step = steps.get(currentStep);
        int[] arr = step.array;
        int n = arr.length;
        if (n == 0) return;

        // Subtle background gridlines
        gc.setStroke(Color.web("#1E293B", 0.45));
        gc.setLineWidth(1.0);
        for (double y = 50; y < h - 45; y += 45) {
            gc.strokeLine(0, y, w, y);
        }

        // Min & Max calculation
        int maxVal = 1;
        int minVal = 0;
        for (int v : arr) {
            if (v > maxVal) maxVal = v;
            if (v < minVal) minVal = v;
        }

        int range = Math.max(1, maxVal - minVal);
        double paddingSide = 25;
        double usableWidth = w - (paddingSide * 2);
        double barGap = n > 50 ? 1 : (n > 30 ? 3 : 6);
        double totalGaps = barGap * (n - 1);
        double barWidth = Math.max(3, (usableWidth - totalGaps) / n);
        double maxBarHeight = h - 95;
        double baseY = h - 45;

        boolean isCompleted = (currentStep == steps.size() - 1);

        double[] barXs = new double[n];
        for (int i = 0; i < n; i++) {
            barXs[i] = paddingSide + i * (barWidth + barGap);
        }

        // Draw Bars
        for (int i = 0; i < n; i++) {
            double normalized = (double) (arr[i] - minVal) / range;
            double barHeight = Math.max(12, normalized * maxBarHeight);
            double x = barXs[i];
            double y = baseY - barHeight;

            boolean isA = (i == step.indexA);
            boolean isB = (i == step.indexB);
            boolean isSpecial = (i == step.specialIndex);

            // High-contrast educational color scheme
            if (isCompleted) {
                LinearGradient grad = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#00F5D4")),
                        new Stop(1, Color.web("#0077B6")));
                gc.setFill(grad);
            } else if (isA) {
                LinearGradient grad = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#FFB703")),
                        new Stop(1, Color.web("#FB8500")));
                gc.setFill(grad);
            } else if (isB) {
                LinearGradient grad = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#FF0054")),
                        new Stop(1, Color.web("#9E0059")));
                gc.setFill(grad);
            } else if (isSpecial) {
                LinearGradient grad = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#9D4EDD")),
                        new Stop(1, Color.web("#5A189A")));
                gc.setFill(grad);
            } else {
                LinearGradient grad = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#00B4D8")),
                        new Stop(1, Color.web("#03045E")));
                gc.setFill(grad);
            }

            gc.fillRoundRect(x, y, barWidth, barHeight, 7, 7);

            // Active bar highlight glow
            if (isA || isB || isSpecial) {
                gc.setStroke(Color.web("#FFFFFF"));
                gc.setLineWidth(2.0);
                gc.strokeRoundRect(x, y, barWidth, barHeight, 7, 7);
            }

            // Number above bar
            if (n <= 35) {
                gc.setFill(isA || isB ? Color.web("#FFB703") : Color.web("#E2E8F0"));
                String label = String.valueOf(arr[i]);
                double textWidth = label.length() * 3.8;
                gc.fillText(label, Math.max(0, x + (barWidth / 2) - textWidth), y - 6);
            }

            // Index below bar: [0], [1]...
            if (n <= 30) {
                gc.setFill(isA || isB ? Color.web("#38BDF8") : Color.web("#64748B"));
                String idxStr = "[" + i + "]";
                double idxWidth = idxStr.length() * 3.4;
                gc.fillText(idxStr, Math.max(0, x + (barWidth / 2) - idxWidth), baseY + 18);
            }
        }

        // Draw overhead indicator
        if (!isCompleted && step.indexA >= 0 && step.indexB >= 0 && step.indexA < n && step.indexB < n) {
            double xA = barXs[step.indexA] + barWidth / 2;
            double xB = barXs[step.indexB] + barWidth / 2;
            double midX = (xA + xB) / 2;
            double arcY = 26;

            boolean isPancakeFlip = (activeAlgo == AlgoDefinition.PANCAKE && step.isSwap);
            boolean isCocktail = (activeAlgo == AlgoDefinition.COCKTAIL);
            boolean isComb = (activeAlgo == AlgoDefinition.COMB);

            gc.setStroke(step.isSwap ? Color.web("#FFB703") : Color.web("#38BDF8"));
            gc.setLineWidth(2.2);
            gc.strokeLine(xA, arcY + 12, xA, arcY);
            gc.strokeLine(xA, arcY, xB, arcY);
            gc.strokeLine(xB, arcY, xB, arcY + 12);

            String badge;
            if (isPancakeFlip) {
                badge = "🥞 FLIPPING PREFIX [0.." + step.indexB + "]";
            } else if (isCocktail && step.isSwap) {
                badge = "🍸 SHAKER SWAP";
            } else if (isComb && step.isSwap) {
                badge = "🪮 GAPPED SWAP";
            } else {
                badge = step.isSwap ? "⇄ SWAPPING" : "COMPARING";
            }

            gc.setFill(step.isSwap ? Color.web("#FB8500") : Color.web("#0284C7"));
            double badgeW = badge.length() * 7.2;
            gc.fillRoundRect(midX - (badgeW / 2) - 6, arcY - 14, badgeW + 12, 20, 6, 6);

            gc.setFill(Color.web("#FFFFFF"));
            gc.fillText(badge, midX - (badgeW / 2), arcY);
        }
    }

    private void goBack() {
        stopAnimation();
        Navigation.slideBack((Parent) backBtn.getScene().getRoot(), "/algexplorer/ui/HubPane.fxml");
    }
}
