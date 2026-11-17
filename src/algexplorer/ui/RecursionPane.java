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
import java.util.List;

public class RecursionPane {

    public enum RecursionMode {
        HANOI("Tower of Hanoi"),
        FIBONACCI("Fibonacci Tree"),
        FACTORIAL("Factorial Call Stack"),
        BINSEARCH("Recursive Binary Search");

        public final String name;
        RecursionMode(String name) { this.name = name; }
    }

    // Step state container
    public static class RecursionStep {
        public final String description;
        public final List<String> callStack;

        // Hanoi state
        public final List<List<Integer>> pegs;
        public final int fromPeg;
        public final int toPeg;
        public final int movedDisk;

        // Fibonacci Tree state: node states by tree ID
        public final int activeNodeId;
        public final List<FibNodeState> fibStates;

        // Binary search state
        public final int low;
        public final int high;
        public final int mid;
        public final boolean found;

        public RecursionStep(String description, List<String> callStack, List<List<Integer>> pegs,
                             int fromPeg, int toPeg, int movedDisk,
                             int activeNodeId, List<FibNodeState> fibStates,
                             int low, int high, int mid, boolean found) {
            this.description = description;
            this.callStack = new ArrayList<>(callStack);
            this.pegs = new ArrayList<>();
            if (pegs != null) {
                for (List<Integer> p : pegs) this.pegs.add(new ArrayList<>(p));
            }
            this.fromPeg = fromPeg;
            this.toPeg = toPeg;
            this.movedDisk = movedDisk;
            this.activeNodeId = activeNodeId;
            this.fibStates = fibStates != null ? new ArrayList<>(fibStates) : null;
            this.low = low;
            this.high = high;
            this.mid = mid;
            this.found = found;
        }
    }

    public static class FibNodeState {
        public final int id;
        public final int n;
        public final int parentId;
        public final int level;
        public final double relX; // 0.0 to 1.0 horizontal position
        public final boolean visited;
        public final boolean resolved;
        public final int returnValue;

        public FibNodeState(int id, int n, int parentId, int level, double relX, boolean visited, boolean resolved, int returnValue) {
            this.id = id;
            this.n = n;
            this.parentId = parentId;
            this.level = level;
            this.relX = relX;
            this.visited = visited;
            this.resolved = resolved;
            this.returnValue = returnValue;
        }
    }

    @FXML private Button backBtn;
    @FXML private Button exitBtn;

    @FXML private Button btnHanoi;
    @FXML private Button btnFib;
    @FXML private Button btnFact;
    @FXML private Button btnBinSearch;

    @FXML private Label inputPromptLabel;
    @FXML private TextField inputN;
    @FXML private Button applyBtn;

    @FXML private Label stackDepthLabel;
    @FXML private Label totalCallsLabel;

    @FXML private Canvas canvas;

    @FXML private Label actionText;
    @FXML private Label stepCounterLabel;

    @FXML private Button playBtn;
    @FXML private Button stepBackBtn;
    @FXML private Button stepForwardBtn;
    @FXML private Button resetBtn;
    @FXML private Slider speedSlider;
    @FXML private Label speedValueLabel;

    private RecursionMode currentMode = RecursionMode.HANOI;
    public static RecursionMode pendingMode = null;
    private List<RecursionStep> steps = new ArrayList<>();
    private int currentStep = 0;
    private Timeline timeline;
    private boolean isPlaying = false;

    // Static array for Binary Search demo
    private final int[] binArray = {4, 9, 15, 23, 31, 38, 45, 52, 60, 68, 77, 85, 92, 99};

    @FXML
    private void initialize() {
        backBtn.setOnAction(e -> goBack());
        exitBtn.setOnAction(e -> {
            if (UIContext.getStage() != null) UIContext.getStage().close();
        });

        btnHanoi.setOnAction(e -> selectMode(RecursionMode.HANOI));
        btnFib.setOnAction(e -> selectMode(RecursionMode.FIBONACCI));
        btnFact.setOnAction(e -> selectMode(RecursionMode.FACTORIAL));
        btnBinSearch.setOnAction(e -> selectMode(RecursionMode.BINSEARCH));

        applyBtn.setOnAction(e -> generateSteps());

        playBtn.setOnAction(e -> togglePlay());
        stepForwardBtn.setOnAction(e -> stepForward());
        stepBackBtn.setOnAction(e -> stepBackward());
        resetBtn.setOnAction(e -> resetToStart());

        speedSlider.setValue(850);
        updateSpeedLabel(850);

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int ms = newVal.intValue();
            updateSpeedLabel(ms);
            if (isPlaying) {
                timeline.stop();
                startTimeline();
            }
        });

        if (pendingMode != null) {
            selectMode(pendingMode);
            pendingMode = null;
        } else {
            selectMode(RecursionMode.HANOI);
        }
    }

    private void updateSpeedLabel(int ms) {
        String pace = ms < 350 ? " (Fast)" : (ms > 1300 ? " (Slow-Mo)" : " (Study Pace)");
        speedValueLabel.setText(ms + " ms" + pace);
    }

    private void selectMode(RecursionMode mode) {
        stopAnimation();
        currentMode = mode;

        btnHanoi.getStyleClass().remove("locked");
        btnFib.getStyleClass().remove("locked");
        btnFact.getStyleClass().remove("locked");
        btnBinSearch.getStyleClass().remove("locked");

        switch (mode) {
            case HANOI -> {
                btnHanoi.getStyleClass().add("locked");
                inputPromptLabel.setText("Disks (3 to 6):");
                inputN.setText("4");
            }
            case FIBONACCI -> {
                btnFib.getStyleClass().add("locked");
                inputPromptLabel.setText("N (3 to 5):");
                inputN.setText("4");
            }
            case FACTORIAL -> {
                btnFact.getStyleClass().add("locked");
                inputPromptLabel.setText("N (3 to 8):");
                inputN.setText("5");
            }
            case BINSEARCH -> {
                btnBinSearch.getStyleClass().add("locked");
                inputPromptLabel.setText("Target Key (1..100):");
                inputN.setText("52");
            }
        }

        generateSteps();
    }

    private void generateSteps() {
        stopAnimation();
        steps.clear();
        int val = 4;
        try {
            val = Integer.parseInt(inputN.getText().trim());
        } catch (NumberFormatException ignored) {}

        switch (currentMode) {
            case HANOI -> {
                int disks = Math.max(3, Math.min(val, 6));
                inputN.setText(String.valueOf(disks));
                buildHanoiSteps(disks);
            }
            case FIBONACCI -> {
                int n = Math.max(2, Math.min(val, 5));
                inputN.setText(String.valueOf(n));
                buildFibonacciSteps(n);
            }
            case FACTORIAL -> {
                int n = Math.max(2, Math.min(val, 8));
                inputN.setText(String.valueOf(n));
                buildFactorialSteps(n);
            }
            case BINSEARCH -> {
                buildBinarySearchSteps(val);
            }
        }

        currentStep = 0;
        updateUI();
        renderCanvas();
    }

    // =========================================================================
    // 1. TOWER OF HANOI
    // =========================================================================
    private void buildHanoiSteps(int disks) {
        List<List<Integer>> pegs = new ArrayList<>();
        pegs.add(new ArrayList<>());
        pegs.add(new ArrayList<>());
        pegs.add(new ArrayList<>());
        for (int i = disks; i >= 1; i--) pegs.get(0).add(i);

        List<String> stack = new ArrayList<>();
        stack.add("hanoi(" + disks + ", A, C, B)");

        int totalMoves = (int) Math.pow(2, disks) - 1;
        steps.add(new RecursionStep("Initial state: " + disks + " disks on Peg A. Optimal moves required: 2^" + disks + " - 1 = " + totalMoves + " moves.",
                stack, pegs, -1, -1, -1, -1, null, -1, -1, -1, false));

        hanoiRec(disks, 0, 2, 1, pegs, stack);

        steps.add(new RecursionStep("🎉 Tower of Hanoi completed! All " + disks + " disks moved from Peg A to Peg C in " + totalMoves + " moves.",
                stack, pegs, -1, -1, -1, -1, null, -1, -1, -1, false));
    }

    private void hanoiRec(int n, int from, int to, int aux, List<List<Integer>> pegs, List<String> stack) {
        char f = (char) ('A' + from);
        char t = (char) ('A' + to);
        char a = (char) ('A' + aux);

        if (n == 1) {
            int disk = pegs.get(from).remove(pegs.get(from).size() - 1);
            pegs.get(to).add(disk);
            stack.add("move(disk 1: " + f + " -> " + t + ")");
            steps.add(new RecursionStep("Base Case n=1: Move smallest disk 1 directly from Peg " + f + " to Peg " + t + ".",
                    stack, pegs, from, to, 1, -1, null, -1, -1, -1, false));
            stack.remove(stack.size() - 1);
            return;
        }

        // Subproblem 1: Move top n-1 disks from source to aux
        stack.add("hanoi(" + (n - 1) + ", " + f + ", " + a + ", " + t + ")");
        steps.add(new RecursionStep("Step 1: Recursively transfer top " + (n - 1) + " disks from Peg " + f + " to Peg " + a + " using Peg " + t + " as buffer.",
                stack, pegs, -1, -1, -1, -1, null, -1, -1, -1, false));
        hanoiRec(n - 1, from, aux, to, pegs, stack);
        stack.remove(stack.size() - 1);

        // Move the nth largest disk from source to destination
        int disk = pegs.get(from).remove(pegs.get(from).size() - 1);
        pegs.get(to).add(disk);
        stack.add("move(disk " + disk + ": " + f + " -> " + t + ")");
        steps.add(new RecursionStep("Step 2: Move disk " + disk + " directly from Peg " + f + " to Peg " + t + ".",
                stack, pegs, from, to, disk, -1, null, -1, -1, -1, false));
        stack.remove(stack.size() - 1);

        // Subproblem 2: Move the n-1 disks from aux to destination
        stack.add("hanoi(" + (n - 1) + ", " + a + ", " + t + ", " + f + ")");
        steps.add(new RecursionStep("Step 3: Recursively transfer the " + (n - 1) + " disks from Peg " + a + " onto Peg " + t + " on top of disk " + disk + ".",
                stack, pegs, -1, -1, -1, -1, null, -1, -1, -1, false));
        hanoiRec(n - 1, aux, to, from, pegs, stack);
        stack.remove(stack.size() - 1);
    }

    // =========================================================================
    // 2. FIBONACCI 2D RECURSION TREE
    // =========================================================================
    private static class RawFibNode {
        int id;
        int n;
        int parentId;
        int level;
        double relX;
        RawFibNode left;
        RawFibNode right;
    }

    private int nextFibId = 0;

    private void buildFibonacciSteps(int n) {
        nextFibId = 0;
        RawFibNode root = createFibTree(n, -1, 0, 0.5, 0.25);
        List<FibNodeState> currentStates = new ArrayList<>();
        populateInitialStates(root, currentStates);

        List<String> stack = new ArrayList<>();
        steps.add(new RecursionStep("Starting Fibonacci Recursion Tree for F(" + n + "). Shows branching calls and overlapping subproblems.",
                stack, null, -1, -1, -1, -1, currentStates, -1, -1, -1, false));

        solveFibTree(root, currentStates, stack);

        steps.add(new RecursionStep("🎉 Fibonacci tree complete! All branches evaluated and return values propagated to root.",
                stack, null, -1, -1, -1, root.id, currentStates, -1, -1, -1, false));
    }

    private RawFibNode createFibTree(int n, int parentId, int level, double relX, double span) {
        RawFibNode node = new RawFibNode();
        node.id = nextFibId++;
        node.n = n;
        node.parentId = parentId;
        node.level = level;
        node.relX = relX;
        if (n > 1) {
            node.left = createFibTree(n - 1, node.id, level + 1, relX - span, span * 0.52);
            node.right = createFibTree(n - 2, node.id, level + 1, relX + span, span * 0.52);
        }
        return node;
    }

    private void populateInitialStates(RawFibNode node, List<FibNodeState> states) {
        if (node == null) return;
        states.add(new FibNodeState(node.id, node.n, node.parentId, node.level, node.relX, false, false, 0));
        populateInitialStates(node.left, states);
        populateInitialStates(node.right, states);
    }

    private int solveFibTree(RawFibNode node, List<FibNodeState> states, List<String> stack) {
        String call = "fib(" + node.n + ")";
        stack.add(call);

        // Mark visited
        updateNodeState(states, node.id, true, false, 0);
        steps.add(new RecursionStep("Invoking " + call + " (Depth " + node.level + ").",
                stack, null, -1, -1, -1, node.id, states, -1, -1, -1, false));

        if (node.n <= 1) {
            // Base case
            int val = node.n;
            updateNodeState(states, node.id, true, true, val);
            steps.add(new RecursionStep("Base Case reached: " + call + " returns " + val + " directly.",
                    stack, null, -1, -1, -1, node.id, states, -1, -1, -1, false));
            stack.remove(stack.size() - 1);
            return val;
        }

        int leftVal = solveFibTree(node.left, states, stack);
        int rightVal = solveFibTree(node.right, states, stack);
        int sum = leftVal + rightVal;

        updateNodeState(states, node.id, true, true, sum);
        steps.add(new RecursionStep(call + " resolved: fib(" + (node.n - 1) + ")=" + leftVal + " + fib(" + (node.n - 2) + ")=" + rightVal + " = " + sum + ".",
                stack, null, -1, -1, -1, node.id, states, -1, -1, -1, false));

        stack.remove(stack.size() - 1);
        return sum;
    }

    private void updateNodeState(List<FibNodeState> states, int id, boolean visited, boolean resolved, int retVal) {
        for (int i = 0; i < states.size(); i++) {
            FibNodeState s = states.get(i);
            if (s.id == id) {
                states.set(i, new FibNodeState(s.id, s.n, s.parentId, s.level, s.relX, visited, resolved, retVal));
                break;
            }
        }
    }

    // =========================================================================
    // 3. FACTORIAL CALL STACK
    // =========================================================================
    private void buildFactorialSteps(int n) {
        List<String> stack = new ArrayList<>();
        steps.add(new RecursionStep("Starting Factorial: Calculating " + n + "! via recursive call stack.", stack, null, -1, -1, -1, -1, null, -1, -1, -1, false));
        int res = factRec(n, stack);
        steps.add(new RecursionStep("🎉 Complete: " + n + "! = " + res + ". Call stack fully resolved and cleared.", stack, null, -1, -1, -1, -1, null, -1, -1, -1, false));
    }

    private int factRec(int n, List<String> stack) {
        String call = "factorial(" + n + ")";
        stack.add(call);
        steps.add(new RecursionStep("Push call frame " + call + " onto stack. Stack depth: " + stack.size() + ".", stack, null, -1, -1, -1, -1, null, -1, -1, -1, false));

        if (n <= 1) {
            steps.add(new RecursionStep("Base Case: factorial(1) = 1. Stack reaches peak depth (" + stack.size() + "). Unwinding begins!", stack, null, -1, -1, -1, -1, null, -1, -1, -1, false));
            stack.remove(stack.size() - 1);
            return 1;
        }

        int sub = factRec(n - 1, stack);
        int result = n * sub;
        steps.add(new RecursionStep("Unwind frame " + call + ": computes " + n + " * factorial(" + (n - 1) + ") = " + n + " * " + sub + " = " + result + ".", stack, null, -1, -1, -1, -1, null, -1, -1, -1, false));
        stack.remove(stack.size() - 1);
        return result;
    }

    // =========================================================================
    // 4. RECURSIVE BINARY SEARCH
    // =========================================================================
    private void buildBinarySearchSteps(int target) {
        List<String> stack = new ArrayList<>();
        steps.add(new RecursionStep("Initiating Recursive Binary Search for target value " + target + " in sorted array.", stack, null, -1, -1, -1, -1, null, 0, binArray.length - 1, -1, false));
        binRec(0, binArray.length - 1, target, stack);
    }

    private int binRec(int low, int high, int target, List<String> stack) {
        String call = "binSearch(low=" + low + ", high=" + high + ")";
        stack.add(call);

        if (low > high) {
            steps.add(new RecursionStep("Base Case: low (" + low + ") > high (" + high + "). Target " + target + " does not exist in array.", stack, null, -1, -1, -1, -1, null, low, high, -1, false));
            stack.remove(stack.size() - 1);
            return -1;
        }

        int mid = low + (high - low) / 2;
        steps.add(new RecursionStep("Inspecting mid index [" + mid + "] = " + binArray[mid] + ". Active range: [" + low + ".." + high + "].", stack, null, -1, -1, -1, -1, null, low, high, mid, false));

        if (binArray[mid] == target) {
            steps.add(new RecursionStep("🎯 SUCCESS: Found target " + target + " at index [" + mid + "]!", stack, null, -1, -1, -1, -1, null, low, high, mid, true));
            stack.remove(stack.size() - 1);
            return mid;
        } else if (binArray[mid] > target) {
            steps.add(new RecursionStep("binArray[" + mid + "] (" + binArray[mid] + ") > target (" + target + "). Eliminating right half. Recursing left: [low=" + low + ".." + (mid - 1) + "].", stack, null, -1, -1, -1, -1, null, low, high, mid, false));
            int res = binRec(low, mid - 1, target, stack);
            stack.remove(stack.size() - 1);
            return res;
        } else {
            steps.add(new RecursionStep("binArray[" + mid + "] (" + binArray[mid] + ") < target (" + target + "). Eliminating left half. Recursing right: [low=" + (mid + 1) + ".." + high + "].", stack, null, -1, -1, -1, -1, null, low, high, mid, false));
            int res = binRec(mid + 1, high, target, stack);
            stack.remove(stack.size() - 1);
            return res;
        }
    }

    // =========================================================================
    // Playback Controls
    // =========================================================================
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
        playBtn.setText("▶ Start Trace");
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
        RecursionStep s = steps.get(currentStep);
        stepCounterLabel.setText("Step " + currentStep + " / " + (steps.size() - 1));
        actionText.setText(s.description);
        stackDepthLabel.setText(String.valueOf(s.callStack.size()));
        totalCallsLabel.setText(String.valueOf(steps.size()));
    }

    // =========================================================================
    // Canvas Rendering
    // =========================================================================
    private void renderCanvas() {
        if (steps.isEmpty() || currentStep >= steps.size()) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        gc.clearRect(0, 0, w, h);

        RecursionStep step = steps.get(currentStep);

        switch (currentMode) {
            case HANOI -> renderHanoi(gc, w, h, step);
            case FIBONACCI -> renderFibTree(gc, w, h, step);
            case FACTORIAL -> renderFactorial(gc, w, h, step);
            case BINSEARCH -> renderBinarySearch(gc, w, h, step);
        }
    }

    private void renderHanoi(GraphicsContext gc, double w, double h, RecursionStep step) {
        double baseY = h - 50;
        double pegWidth = 12;
        double pegHeight = 180;
        double[] pegX = {w * 0.22, w * 0.50, w * 0.78};
        String[] pegNames = {"Peg A (Source)", "Peg B (Aux)", "Peg C (Destination)"};

        // Base plate
        gc.setFill(Color.web("#1E293B"));
        gc.fillRoundRect(w * 0.1, baseY, w * 0.8, 16, 8, 8);

        for (int p = 0; p < 3; p++) {
            // Peg Pillar
            gc.setFill(Color.web("#334155"));
            gc.fillRoundRect(pegX[p] - pegWidth / 2, baseY - pegHeight, pegWidth, pegHeight, 6, 6);

            // Peg Label
            gc.setFill(Color.web("#94A3B8"));
            gc.fillText(pegNames[p], pegX[p] - 50, baseY + 32);

            // Disks
            List<Integer> disks = step.pegs.get(p);
            double diskH = 22;
            for (int d = 0; d < disks.size(); d++) {
                int diskSize = disks.get(d);
                double diskW = 40 + (diskSize * 26);
                double dx = pegX[p] - (diskW / 2);
                double dy = baseY - ((d + 1) * (diskH + 3));

                boolean isMoved = (p == step.toPeg && diskSize == step.movedDisk);

                LinearGradient grad = isMoved
                        ? new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#FFB703")), new Stop(1, Color.web("#FB8500")))
                        : new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#00F5D4")), new Stop(1, Color.web("#00BBF9")));

                gc.setFill(grad);
                gc.fillRoundRect(dx, dy, diskW, diskH, 8, 8);

                gc.setFill(Color.web("#070B12"));
                gc.fillText("Disk " + diskSize, dx + (diskW / 2) - 16, dy + 15);
            }
        }
    }

    private void renderFibTree(GraphicsContext gc, double w, double h, RecursionStep step) {
        if (step.fibStates == null) return;

        // Draw connections between parent and child nodes
        for (FibNodeState s : step.fibStates) {
            if (s.parentId >= 0) {
                FibNodeState parent = findFibState(step.fibStates, s.parentId);
                if (parent != null) {
                    double px = parent.relX * w;
                    double py = 45 + (parent.level * 60);
                    double cx = s.relX * w;
                    double cy = 45 + (s.level * 60);

                    gc.setStroke(s.visited ? Color.web("#00F5D4", 0.7) : Color.web("#1E293B"));
                    gc.setLineWidth(2.0);
                    gc.strokeLine(px, py, cx, cy);
                }
            }
        }

        // Draw Nodes
        double radius = 19;
        for (FibNodeState s : step.fibStates) {
            double x = s.relX * w;
            double y = 45 + (s.level * 60);

            boolean isActive = (s.id == step.activeNodeId);

            if (isActive) {
                // Active calling node: Amber Glow
                LinearGradient grad = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#FFB703")), new Stop(1, Color.web("#FB8500")));
                gc.setFill(grad);
            } else if (s.resolved) {
                // Resolved node: Emerald Green
                LinearGradient grad = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#00F5D4")), new Stop(1, Color.web("#0077B6")));
                gc.setFill(grad);
            } else if (s.visited) {
                // Visited waiting node: Cyan
                LinearGradient grad = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#38BDF8")), new Stop(1, Color.web("#0284C7")));
                gc.setFill(grad);
            } else {
                // Unvisited node: Dark Slate
                gc.setFill(Color.web("#1E293B"));
            }

            gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);

            gc.setStroke(isActive ? Color.web("#FFFFFF") : (s.resolved ? Color.web("#00F5D4") : Color.web("#334155")));
            gc.setLineWidth(1.5);
            gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);

            // Node text: F(n) or =result
            gc.setFill(s.visited ? Color.web("#070B12") : Color.web("#94A3B8"));
            String label = s.resolved ? "=" + s.returnValue : "F(" + s.n + ")";
            gc.fillText(label, x - (label.length() * 3.6), y + 5);
        }
    }

    private FibNodeState findFibState(List<FibNodeState> states, int id) {
        for (FibNodeState s : states) {
            if (s.id == id) return s;
        }
        return null;
    }

    private void renderFactorial(GraphicsContext gc, double w, double h, RecursionStep step) {
        double frameW = 340;
        double frameH = 34;
        double startX = (w - frameW) / 2;
        double startY = h - 45;

        gc.setFill(Color.web("#94A3B8"));
        gc.fillText("FACTORIAL CALL STACK CONTAINER (LIFO)", startX, 30);

        if (step.callStack.isEmpty()) {
            gc.setFill(Color.web("#475569"));
            gc.fillText("[ Call stack resolved ]", startX + 90, 80);
            return;
        }

        for (int i = 0; i < step.callStack.size(); i++) {
            double y = startY - ((i + 1) * (frameH + 8));
            boolean isTop = (i == step.callStack.size() - 1);

            LinearGradient grad = isTop
                    ? new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#FFB703")), new Stop(1, Color.web("#FB8500")))
                    : new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#1E293B")), new Stop(1, Color.web("#0F172A")));

            gc.setFill(grad);
            gc.fillRoundRect(startX, y, frameW, frameH, 8, 8);

            gc.setStroke(isTop ? Color.web("#FFFFFF") : Color.web("#334155"));
            gc.setLineWidth(1.5);
            gc.strokeRoundRect(startX, y, frameW, frameH, 8, 8);

            gc.setFill(isTop ? Color.web("#070B12") : Color.web("#E2E8F0"));
            gc.fillText("Frame " + (i + 1) + ": " + step.callStack.get(i), startX + 16, y + 21);

            if (isTop) {
                gc.setFill(Color.web("#00F5D4"));
                gc.fillText("← TOP OF STACK", startX + frameW + 12, y + 21);
            }
        }
    }

    private void renderBinarySearch(GraphicsContext gc, double w, double h, RecursionStep step) {
        int n = binArray.length;
        double pad = 40;
        double usableW = w - (pad * 2);
        double boxW = (usableW - ((n - 1) * 8)) / n;
        double boxH = 46;
        double y = h * 0.44;

        gc.setFill(Color.web("#94A3B8"));
        gc.fillText("SORTED ARRAY RECURSIVE SEARCH BOUNDARIES", pad, y - 50);

        for (int i = 0; i < n; i++) {
            double x = pad + i * (boxW + 8);
            boolean inRange = (i >= step.low && i <= step.high);
            boolean isMid = (i == step.mid);

            if (step.found && isMid) {
                // Target found: Emerald Glow
                LinearGradient grad = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#00F5D4")), new Stop(1, Color.web("#0077B6")));
                gc.setFill(grad);
            } else if (isMid) {
                // Mid point being tested: Amber
                LinearGradient grad = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#FFB703")), new Stop(1, Color.web("#FB8500")));
                gc.setFill(grad);
            } else if (inRange) {
                // In active search range: Cyan
                LinearGradient grad = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#1E293B")), new Stop(1, Color.web("#0F172A")));
                gc.setFill(grad);
            } else {
                // Eliminated elements: Dim dark grey
                gc.setFill(Color.web("#0B0F19"));
            }

            gc.fillRoundRect(x, y, boxW, boxH, 8, 8);

            gc.setStroke(isMid ? Color.web("#FFFFFF") : (inRange ? Color.web("#00BBF9") : Color.web("#1E293B")));
            gc.setLineWidth(isMid ? 2.0 : 1.0);
            gc.strokeRoundRect(x, y, boxW, boxH, 8, 8);

            // Value text
            gc.setFill(isMid ? Color.web("#070B12") : (inRange ? Color.web("#FFFFFF") : Color.web("#475569")));
            String valStr = String.valueOf(binArray[i]);
            gc.fillText(valStr, x + (boxW / 2) - (valStr.length() * 4), y + 28);

            // Index below box
            gc.setFill(Color.web("#64748B"));
            String idxStr = "[" + i + "]";
            gc.fillText(idxStr, x + (boxW / 2) - (idxStr.length() * 3.5), y + boxH + 18);

            // Draw LOW, MID, HIGH pointers
            if (i == step.low && step.low <= step.high) {
                gc.setFill(Color.web("#00F5D4"));
                gc.fillText("▲ LOW", x + 5, y - 10);
            }
            if (i == step.high && step.low <= step.high) {
                gc.setFill(Color.web("#38BDF8"));
                gc.fillText("▲ HIGH", x + 5, y - 26);
            }
            if (isMid) {
                gc.setFill(Color.web("#FFB703"));
                gc.fillText("▼ MID", x + 5, y + boxH + 36);
            }
        }
    }

    private void goBack() {
        stopAnimation();
        Navigation.slideBack((Parent) backBtn.getScene().getRoot(), "/algexplorer/ui/HubPane.fxml");
    }
}
