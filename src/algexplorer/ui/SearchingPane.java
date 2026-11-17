package algexplorer.ui;

import algexplorer.UIContext;
import algexplorer.navigation.Navigation;
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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SearchingPane {

    public enum SearchAlgo {
        LINEAR("Linear Search", "O(n)", "O(1)"),
        BINARY("Binary Search", "O(log n)", "O(1)"),
        JUMP("Jump Search", "O(√n)", "O(1)"),
        INTERPOLATION("Interpolation Search", "O(log log n)", "O(1)");

        public final String name;
        public final String timeComp;
        public final String spaceComp;

        SearchAlgo(String name, String timeComp, String spaceComp) {
            this.name = name;
            this.timeComp = timeComp;
            this.spaceComp = spaceComp;
        }
    }

    public static class SearchStep {
        public final int[] array;
        public final int activeIndex;
        public final int low;
        public final int high;
        public final boolean found;
        public final String description;
        public final int checks;

        public SearchStep(int[] array, int activeIndex, int low, int high, boolean found, String description, int checks) {
            this.array = Arrays.copyOf(array, array.length);
            this.activeIndex = activeIndex;
            this.low = low;
            this.high = high;
            this.found = found;
            this.description = description;
            this.checks = checks;
        }
    }

    @FXML private Button backBtn;
    @FXML private Button exitBtn;

    @FXML private Label algoNameStat;
    @FXML private Label algoComplexityStat;

    @FXML private Button btnLinear;
    @FXML private Button btnBinary;
    @FXML private Button btnJump;
    @FXML private Button btnInterpolation;

    @FXML private TextField targetInput;
    @FXML private Button applyTargetBtn;

    @FXML private TextField arrayInput;
    @FXML private Button loadArrayBtn;
    @FXML private Button randomArrayBtn;

    @FXML private Canvas canvas;

    @FXML private Label actionText;
    @FXML private Label stepCounterLabel;

    @FXML private Button playBtn;
    @FXML private Button stepBackBtn;
    @FXML private Button stepForwardBtn;
    @FXML private Button resetBtn;
    @FXML private Slider speedSlider;
    @FXML private Label speedValueLabel;
    @FXML private Label checksLabel;

    private SearchAlgo activeAlgo = SearchAlgo.LINEAR;
    public static SearchAlgo pendingAlgo = null;
    private int[] searchArray;
    private int targetKey = 52;
    private List<SearchStep> steps = new ArrayList<>();
    private int currentStep = 0;
    private Timeline timeline;
    private boolean isPlaying = false;

    @FXML
    private void initialize() {
        backBtn.setOnAction(e -> goBack());
        exitBtn.setOnAction(e -> {
            if (UIContext.getStage() != null) UIContext.getStage().close();
        });

        btnLinear.setOnAction(e -> selectAlgo(SearchAlgo.LINEAR));
        btnBinary.setOnAction(e -> selectAlgo(SearchAlgo.BINARY));
        btnJump.setOnAction(e -> selectAlgo(SearchAlgo.JUMP));
        btnInterpolation.setOnAction(e -> selectAlgo(SearchAlgo.INTERPOLATION));

        applyTargetBtn.setOnAction(e -> {
            try {
                targetKey = Integer.parseInt(targetInput.getText().trim());
                recalculateSteps();
            } catch (NumberFormatException ignored) {}
        });

        loadArrayBtn.setOnAction(e -> parseArrayInput());
        randomArrayBtn.setOnAction(e -> generateRandomSorted());

        playBtn.setOnAction(e -> togglePlay());
        stepForwardBtn.setOnAction(e -> stepForward());
        stepBackBtn.setOnAction(e -> stepBackward());
        resetBtn.setOnAction(e -> resetToStart());

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            speedValueLabel.setText(newVal.intValue() + " ms");
            if (isPlaying) {
                timeline.stop();
                startTimeline();
            }
        });

        if (pendingAlgo != null) {
            selectAlgo(pendingAlgo);
            pendingAlgo = null;
        } else {
            selectAlgo(SearchAlgo.LINEAR);
        }
        parseArrayInput();
    }

    private void selectAlgo(SearchAlgo algo) {
        stopAnimation();
        activeAlgo = algo;

        btnLinear.getStyleClass().remove("locked");
        btnBinary.getStyleClass().remove("locked");
        btnJump.getStyleClass().remove("locked");
        btnInterpolation.getStyleClass().remove("locked");

        switch (algo) {
            case LINEAR -> btnLinear.getStyleClass().add("locked");
            case BINARY -> btnBinary.getStyleClass().add("locked");
            case JUMP -> btnJump.getStyleClass().add("locked");
            case INTERPOLATION -> btnInterpolation.getStyleClass().add("locked");
        }

        algoNameStat.setText(algo.name);
        algoComplexityStat.setText(algo.timeComp);
        actionText.setText("Selected " + algo.name + " (" + algo.timeComp + "). Target key: " + targetKey + ".");

        recalculateSteps();
    }

    private void parseArrayInput() {
        stopAnimation();
        String text = arrayInput.getText().trim();
        String[] tokens = text.split("[,\\s]+");
        List<Integer> list = new ArrayList<>();
        for (String t : tokens) {
            if (t.isEmpty()) continue;
            try {
                list.add(Integer.parseInt(t));
            } catch (NumberFormatException ignored) {}
        }

        if (list.isEmpty()) {
            list = List.of(5, 12, 19, 24, 31, 38, 45, 52, 60, 68, 77, 85, 93, 102, 115, 128);
        }

        searchArray = new int[list.size()];
        for (int i = 0; i < list.size(); i++) searchArray[i] = list.get(i);
        Arrays.sort(searchArray); // Ensure sorted for search algos

        recalculateSteps();
    }

    private void generateRandomSorted() {
        Random rng = new Random();
        int count = 16;
        int[] arr = new int[count];
        int val = 5;
        for (int i = 0; i < count; i++) {
            val += 4 + rng.nextInt(9);
            arr[i] = val;
        }
        searchArray = arr;

        // Pick one existing value or random target
        targetKey = arr[rng.nextInt(count)];
        targetInput.setText(String.valueOf(targetKey));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(arr[i]);
            if (i < count - 1) sb.append(", ");
        }
        arrayInput.setText(sb.toString());

        recalculateSteps();
    }

    private void recalculateSteps() {
        stopAnimation();
        if (searchArray == null || searchArray.length == 0) return;

        steps.clear();
        switch (activeAlgo) {
            case LINEAR -> buildLinearSearch();
            case BINARY -> buildBinarySearch();
            case JUMP -> buildJumpSearch();
            case INTERPOLATION -> buildInterpolationSearch();
        }

        currentStep = 0;
        updateUI();
        renderCanvas();
    }

    // 1. Linear Search
    private void buildLinearSearch() {
        int checks = 0;
        int n = searchArray.length;
        steps.add(new SearchStep(searchArray, -1, 0, n - 1, false,
                "Initial array: Linear Search checks each index from 0 to " + (n - 1) + " sequentially.", 0));

        for (int i = 0; i < n; i++) {
            checks++;
            if (searchArray[i] == targetKey) {
                steps.add(new SearchStep(searchArray, i, 0, n - 1, true,
                        "🎯 Target " + targetKey + " FOUND at index [" + i + "] after " + checks + " checks!", checks));
                return;
            } else {
                steps.add(new SearchStep(searchArray, i, 0, n - 1, false,
                        "Inspecting arr[" + i + "] = " + searchArray[i] + ". (" + searchArray[i] + " != target " + targetKey + ") -> Advance to next index.", checks));
            }
        }
        steps.add(new SearchStep(searchArray, -1, 0, n - 1, false,
                "❌ Target " + targetKey + " was not found in the array after checking all " + checks + " elements.", checks));
    }

    // 2. Binary Search
    private void buildBinarySearch() {
        int checks = 0;
        int n = searchArray.length;
        int low = 0, high = n - 1;
        steps.add(new SearchStep(searchArray, -1, low, high, false,
                "Initial array: Binary Search divides active search window [low..high] in half each step.", 0));

        while (low <= high) {
            checks++;
            int mid = low + (high - low) / 2;
            int midVal = searchArray[mid];

            if (midVal == targetKey) {
                steps.add(new SearchStep(searchArray, mid, low, high, true,
                        "🎯 Target " + targetKey + " FOUND at mid index [" + mid + "] in " + checks + " probes!", checks));
                return;
            } else if (midVal < targetKey) {
                steps.add(new SearchStep(searchArray, mid, low, high, false,
                        "Probe arr[" + mid + "] = " + midVal + " < target " + targetKey + ". Target must be in right half. Adjusting low = " + (mid + 1) + ".", checks));
                low = mid + 1;
            } else {
                steps.add(new SearchStep(searchArray, mid, low, high, false,
                        "Probe arr[" + mid + "] = " + midVal + " > target " + targetKey + ". Target must be in left half. Adjusting high = " + (mid - 1) + ".", checks));
                high = mid - 1;
            }
        }
        steps.add(new SearchStep(searchArray, -1, low, high, false,
                "❌ Target " + targetKey + " not found (low > high). Search window empty.", checks));
    }

    // 3. Jump Search
    private void buildJumpSearch() {
        int checks = 0;
        int n = searchArray.length;
        int step = (int) Math.floor(Math.sqrt(n));
        int prev = 0;

        steps.add(new SearchStep(searchArray, -1, 0, n - 1, false,
                "Jump Search initialized with optimal block step size m = √" + n + " ≈ " + step + ".", 0));

        // Finding block where element is present
        while (prev < n && searchArray[Math.min(step, n) - 1] < targetKey) {
            checks++;
            int checkIdx = Math.min(step, n) - 1;
            steps.add(new SearchStep(searchArray, checkIdx, prev, Math.min(step, n) - 1, false,
                    "Jump block [" + prev + ".." + checkIdx + "]: arr[" + checkIdx + "] = " + searchArray[checkIdx] + " < " + targetKey + ". Jumping to next block.", checks));
            prev = step;
            step += (int) Math.floor(Math.sqrt(n));
            if (prev >= n) break;
        }

        // Linear search within identified block
        int limit = Math.min(step, n);
        steps.add(new SearchStep(searchArray, -1, prev, limit - 1, false,
                "Target is within block range [" + prev + ".." + (limit - 1) + "]. Performing linear scan.", checks));

        while (prev < limit) {
            checks++;
            if (searchArray[prev] == targetKey) {
                steps.add(new SearchStep(searchArray, prev, prev, limit - 1, true,
                        "🎯 Target " + targetKey + " FOUND at index [" + prev + "] in " + checks + " checks!", checks));
                return;
            } else if (searchArray[prev] > targetKey) {
                steps.add(new SearchStep(searchArray, prev, prev, limit - 1, false,
                        "arr[" + prev + "] = " + searchArray[prev] + " > target " + targetKey + ". Target not present.", checks));
                return;
            }
            steps.add(new SearchStep(searchArray, prev, prev, limit - 1, false,
                    "Inspecting block element arr[" + prev + "] = " + searchArray[prev] + ".", checks));
            prev++;
        }

        steps.add(new SearchStep(searchArray, -1, 0, n - 1, false,
                "❌ Target " + targetKey + " not found after jump & block search.", checks));
    }

    // 4. Interpolation Search
    private void buildInterpolationSearch() {
        int checks = 0;
        int n = searchArray.length;
        int low = 0, high = n - 1;

        steps.add(new SearchStep(searchArray, -1, low, high, false,
                "Interpolation Search initialized: Calculates probe position using linear interpolation formula based on target value.", 0));

        while (low <= high && targetKey >= searchArray[low] && targetKey <= searchArray[high]) {
            checks++;
            if (low == high) {
                if (searchArray[low] == targetKey) {
                    steps.add(new SearchStep(searchArray, low, low, high, true,
                            "🎯 Target " + targetKey + " FOUND at index [" + low + "]!", checks));
                }
                return;
            }

            // Estimate position: pos = low + ((target - arr[low]) * (high - low)) / (arr[high] - arr[low])
            long numerator = (long) (targetKey - searchArray[low]) * (high - low);
            long denominator = (long) (searchArray[high] - searchArray[low]);
            int pos = (int) (low + (numerator / denominator));

            if (pos < low || pos > high) break;

            int probeVal = searchArray[pos];
            if (probeVal == targetKey) {
                steps.add(new SearchStep(searchArray, pos, low, high, true,
                        "🎯 Formula probe matched! Target " + targetKey + " FOUND at index [" + pos + "] in " + checks + " probes!", checks));
                return;
            } else if (probeVal < targetKey) {
                steps.add(new SearchStep(searchArray, pos, low, high, false,
                        "Formula probed arr[" + pos + "] = " + probeVal + " < " + targetKey + ". Adjusting low = " + (pos + 1) + ".", checks));
                low = pos + 1;
            } else {
                steps.add(new SearchStep(searchArray, pos, low, high, false,
                        "Formula probed arr[" + pos + "] = " + probeVal + " > " + targetKey + ". Adjusting high = " + (pos - 1) + ".", checks));
                high = pos - 1;
            }
        }

        steps.add(new SearchStep(searchArray, -1, low, high, false,
                "❌ Target " + targetKey + " not found. Probe formula exceeded bounds.", checks));
    }

    // Playback
    private void togglePlay() {
        if (isPlaying) stopAnimation();
        else startAnimation();
    }

    private void startAnimation() {
        if (steps.isEmpty()) return;
        if (currentStep >= steps.size() - 1) currentStep = 0;
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
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void stopAnimation() {
        if (timeline != null) timeline.stop();
        isPlaying = false;
        playBtn.setText("▶ Start Search");
    }

    private void stepForward() {
        stopAnimation();
        if (currentStep < steps.size() - 1) {
            currentStep++;
            updateUI();
            renderCanvas();
        }
    }

    private void stepBackward() {
        stopAnimation();
        if (currentStep > 0) {
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
    }

    private void updateUI() {
        if (steps.isEmpty()) return;
        SearchStep s = steps.get(currentStep);
        stepCounterLabel.setText("Step " + currentStep + " / " + (steps.size() - 1));
        actionText.setText(s.description);
        checksLabel.setText(String.valueOf(s.checks));
    }

    private void renderCanvas() {
        if (steps.isEmpty() || currentStep >= steps.size()) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        gc.clearRect(0, 0, w, h);

        SearchStep step = steps.get(currentStep);
        int[] arr = step.array;
        int n = arr.length;
        if (n == 0) return;

        double pad = 35;
        double usableW = w - (pad * 2);
        double boxGap = n > 25 ? 2 : 6;
        double boxW = Math.max(16, (usableW - ((n - 1) * boxGap)) / n);
        double boxH = 50;
        double y = h * 0.44;

        for (int i = 0; i < n; i++) {
            double x = pad + i * (boxW + boxGap);
            boolean inRange = (i >= step.low && i <= step.high);
            boolean isActive = (i == step.activeIndex);

            if (step.found && isActive) {
                // Target Found: Glowing Emerald
                LinearGradient grad = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#00F5D4")), new Stop(1, Color.web("#0077B6")));
                gc.setFill(grad);
            } else if (isActive) {
                // Currently Checked Probe: Amber
                LinearGradient grad = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#FFB703")), new Stop(1, Color.web("#FB8500")));
                gc.setFill(grad);
            } else if (inRange) {
                // In search window: Dark Slate Blue
                LinearGradient grad = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#1E293B")), new Stop(1, Color.web("#0F172A")));
                gc.setFill(grad);
            } else {
                // Out of range (eliminated): Dim Grey
                gc.setFill(Color.web("#0B0F19"));
            }

            gc.fillRoundRect(x, y, boxW, boxH, 8, 8);

            gc.setStroke(isActive ? Color.web("#FFFFFF") : (inRange ? Color.web("#00BBF9") : Color.web("#1E293B")));
            gc.setLineWidth(isActive ? 2.2 : 1.0);
            gc.strokeRoundRect(x, y, boxW, boxH, 8, 8);

            // Number value
            gc.setFill(isActive ? Color.web("#070B12") : (inRange ? Color.web("#FFFFFF") : Color.web("#475569")));
            String valStr = String.valueOf(arr[i]);
            gc.fillText(valStr, x + (boxW / 2) - (valStr.length() * 3.8), y + 30);

            // Index label
            gc.setFill(Color.web("#64748B"));
            String idxStr = "[" + i + "]";
            gc.fillText(idxStr, x + (boxW / 2) - (idxStr.length() * 3.2), y + boxH + 18);

            // Pointers
            if (i == step.low && step.low >= 0 && step.low <= step.high) {
                gc.setFill(Color.web("#00F5D4"));
                gc.fillText("LOW ▲", x, y - 8);
            }
            if (i == step.high && step.high >= 0 && step.low <= step.high) {
                gc.setFill(Color.web("#38BDF8"));
                gc.fillText("HIGH ▲", x, y - 24);
            }
            if (isActive) {
                gc.setFill(step.found ? Color.web("#00F5D4") : Color.web("#FFB703"));
                gc.fillText("▼ CHECK", x, y + boxH + 34);
            }
        }
    }

    private void goBack() {
        stopAnimation();
        Navigation.slideBack((Parent) backBtn.getScene().getRoot(), "/algexplorer/ui/HubPane.fxml");
    }
}
